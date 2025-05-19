package tescha.Components;

import animatefx.animation.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Advanced Toast Notification Component for JavaFX applications.
 * Provides rich, customizable toast notifications with various styles, animations, and interaction options.
 */
public class AdvancedToast {

    // Configuration
    private static final int MAX_TOASTS_PER_POSITION = 4;
    private static final double TOAST_WIDTH = 320;
    private static final double TOAST_MIN_HEIGHT = 60;
    private static final double MARGIN = 15;
    private static final double SPACING = 10;
    private static final double PROGRESS_BAR_HEIGHT = 3;

    // Queue and control
    private static final ConcurrentLinkedQueue<ToastData> toastQueue = new ConcurrentLinkedQueue<>();
    private static final AtomicBoolean isProcessingQueue = new AtomicBoolean(false);
    private static final Map<ToastPosition, List<Popup>> activeToasts = new HashMap<>();
    private static final Map<ToastPosition, Double> positionOffsets = new HashMap<>();
    private static QueueStrategy queueStrategy = QueueStrategy.STACK;

    static {
        for (ToastPosition position : ToastPosition.values()) {
            activeToasts.put(position, new ArrayList<>());
            positionOffsets.put(position, 0.0);
        }
    }

    /**
     * Data class for toast notification configuration.
     */
    public static class ToastData {
        final Node parent;
        final String title;
        final String message;
        final ToastType type;
        final ToastPosition position;
        final int duration;
        final boolean persistent;
        final List<ToastAction> actions;
        final Runnable onClosed;
        final Node customIcon;
        final boolean richText;
        final boolean showCloseButton;
        final Consumer<Button> confirmCallback;
        final Consumer<Button> denyCallback;

        ToastData(Builder builder) {
            this.parent = builder.parent;
            this.title = builder.title;
            this.message = builder.message;
            this.type = builder.type;
            this.position = builder.position;
            this.duration = builder.duration;
            this.persistent = builder.persistent;
            this.actions = builder.actions;
            this.onClosed = builder.onClosed;
            this.customIcon = builder.customIcon;
            this.richText = builder.richText;
            this.showCloseButton = builder.showCloseButton;
            this.confirmCallback = builder.confirmCallback;
            this.denyCallback = builder.denyCallback;
        }

        /**
         * Show this toast notification.
         */
        public void show() {
            AdvancedToast.showToast(this);
        }

        /**
         * Builder for creating ToastData instances with a fluent API.
         */
        public static class Builder {
            private Node parent;
            private String title;
            private String message;
            private ToastType type = ToastType.INFO;
            private ToastPosition position = ToastPosition.TOP_RIGHT;
            private int duration = 5;
            private boolean persistent = false;
            private List<ToastAction> actions = new ArrayList<>();
            private Runnable onClosed;
            private Node customIcon;
            private boolean richText = false;
            private boolean showCloseButton = true;
            private Consumer<Button> confirmCallback;
            private Consumer<Button> denyCallback;

            public Builder(Node parent) {
                this.parent = parent;
            }

            public Builder title(String title) {
                this.title = title;
                return this;
            }

            public         Builder message(String message) {
                this.message = message;
                return this;
            }

            public   Builder type(ToastType type) {
                this.type = type;
                return this;
            }

            public  Builder position(ToastPosition position) {
                this.position = position;
                return this;
            }

            public  Builder duration(int duration) {
                this.duration = duration;
                return this;
            }

            public    Builder persistent(boolean persistent) {
                this.persistent = persistent;
                return this;
            }

            public       Builder addAction(ToastAction action) {
                this.actions.add(action);
                return this;
            }

            public   Builder actions(List<ToastAction> actions) {
                this.actions = actions;
                return this;
            }

            public    Builder onClosed(Runnable onClosed) {
                this.onClosed = onClosed;
                return this;
            }

            public   Builder customIcon(Node icon) {
                this.customIcon = icon;
                return this;
            }

            public     Builder richText(boolean richText) {
                this.richText = richText;
                return this;
            }

            public  Builder showCloseButton(boolean show) {
                this.showCloseButton = show;
                return this;
            }

            public    Builder onConfirm(Consumer<Button> callback) {
                this.confirmCallback = callback;
                return this;
            }

            public   Builder onDeny(Consumer<Button> callback) {
                this.denyCallback = callback;
                return this;
            }

            public    ToastData build() {
                // For critical toasts, enforce certain settings
                if (type == ToastType.CRITICAL) {
                    this.persistent = true;
                    this.showCloseButton = false;

                    // Add confirm/deny buttons if not already present
                    if (actions.isEmpty() && (confirmCallback != null || denyCallback != null)) {
                        if (confirmCallback != null) {
                            actions.add(new ToastAction("Confirm", ButtonType.CONFIRM, confirmCallback));
                        }
                        if (denyCallback != null) {
                            actions.add(new ToastAction("Dismiss", ButtonType.CANCEL, denyCallback));
                        }
                    }
                }

                return new ToastData(this);
            }
        }
    }

    /**
     * Represents an action button in a toast notification.
     */
    public static class ToastAction {
        private final String text;
        private final ButtonType type;
        private final Consumer<Button> action;

        public ToastAction(String text, ButtonType type, Consumer<Button> action) {
            this.text = text;
            this.type = type;
            this.action = action;
        }

        public String getText() {
            return text;
        }

        public ButtonType getType() {
            return type;
        }

        public Consumer<Button> getAction() {
            return action;
        }
    }

    public enum ToastType { SUCCESS, ERROR, INFO, WARNING, CUSTOM, CRITICAL }
    public enum ToastPosition { TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT }
    public enum QueueStrategy { STACK, REPLACE_SAME_TYPE, REPLACE_ALL }
    public enum ButtonType { PRIMARY, SECONDARY, SUCCESS, DANGER, WARNING, INFO, CONFIRM, CANCEL }

    /**
     * Set the queue strategy for handling new toasts when maximum is reached.
     * @param strategy The strategy to use
     */
    public static void setQueueStrategy(QueueStrategy strategy) {
        queueStrategy = strategy;
    }

    // Simplified public methods for common toast types

    /**
     * Show a success toast notification.
     * @param parent The parent node
     * @param title The toast title
     * @param message The toast message
     */
    public static void showSuccessToast(Node parent, String title, String message) {
        new ToastData.Builder(parent)
                .title(title)
                .message(message)
                .type(ToastType.SUCCESS)
                .duration(5)
                .build()
                .show();
    }

    /**
     * Show an error toast notification.
     * @param parent The parent node
     * @param title The toast title
     * @param message The toast message
     */
    public static void showErrorToast(Node parent, String title, String message) {
        new ToastData.Builder(parent)
                .title(title)
                .message(message)
                .type(ToastType.ERROR)
                .duration(0)
                .persistent(true)
                .build()
                .show();
    }

    /**
     * Show an info toast notification.
     * @param parent The parent node
     * @param title The toast title
     * @param message The toast message
     */
    public static void showInfoToast(Node parent, String title, String message) {
        new ToastData.Builder(parent)
                .title(title)
                .message(message)
                .type(ToastType.INFO)
                .duration(5)
                .build()
                .show();
    }

    /**
     * Show a warning toast notification.
     * @param parent The parent node
     * @param title The toast title
     * @param message The toast message
     */
    public static void showWarningToast(Node parent, String title, String message) {
        new ToastData.Builder(parent)
                .title(title)
                .message(message)
                .type(ToastType.WARNING)
                .duration(7)
                .build()
                .show();
    }

    /**
     * Show a critical toast notification that requires user confirmation.
     * @param parent The parent node
     * @param title The toast title
     * @param message The toast message
     * @param onConfirm Callback when user confirms
     * @param onDeny Callback when user denies
     */
    public static void showCriticalToast(Node parent, String title, String message,
                                         Consumer<Button> onConfirm, Consumer<Button> onDeny) {
        new ToastData.Builder(parent)
                .title(title)
                .message(message)
                .type(ToastType.CRITICAL)
                .persistent(true)
                .showCloseButton(false)
                .onConfirm(onConfirm)
                .onDeny(onDeny)
                .build()
                .show();
    }

    /**
     * Show a rich text toast notification with HTML-like formatting.
     * @param parent The parent node
     * @param title The toast title
     * @param richMessage The rich text message (supports basic HTML tags)
     * @param type The toast type
     */
    public static void showRichToast(Node parent, String title, String richMessage, ToastType type) {
        new ToastData.Builder(parent)
                .title(title)
                .message(richMessage)
                .type(type)
                .richText(true)
                .duration(7)
                .build()
                .show();
    }

    /**
     * Show a custom toast with an image icon.
     * @param parent The parent node
     * @param title The toast title
     * @param message The toast message
     * @param imagePath Path to the image for the icon
     */
    public static void showImageToast(Node parent, String title, String message, String imagePath) {
        try {
            ImageView imageView = new ImageView(new Image(imagePath));
            imageView.setFitHeight(24);
            imageView.setFitWidth(24);
            imageView.setPreserveRatio(true);

            new ToastData.Builder(parent)
                    .title(title)
                    .message(message)
                    .type(ToastType.CUSTOM)
                    .customIcon(imageView)
                    .duration(5)
                    .build()
                    .show();
        } catch (Exception e) {
            System.err.println("Failed to load image for toast: " + e.getMessage());
            showInfoToast(parent, title, message);
        }
    }

    /**
     * Show a toast notification with the given configuration.
     * @param data The toast data
     */
    private static void showToast(ToastData data) {
        if (data.parent == null || data.parent.getScene() == null || data.parent.getScene().getWindow() == null) {
            System.err.println("Cannot show toast: parent node, scene, or window is null");
            return;
        }

        // Ensure CSS is loaded
        if (!data.parent.getScene().getStylesheets().contains("styles/toast.css")) {
            try {
                data.parent.getScene().getStylesheets().add("styles/toast.css");
            } catch (Exception e) {
                System.err.println("Failed to load toast.css: " + e.getMessage());
            }
        }

        toastQueue.add(data);
        processQueueIfNeeded();
    }

    private static void processQueueIfNeeded() {
        if (isProcessingQueue.compareAndSet(false, true)) {
            Platform.runLater(AdvancedToast::processQueue);
        }
    }

    private static void processQueue() {
        ToastData data = toastQueue.poll();
        if (data != null) {
            // Apply queue strategy if we've reached the maximum number of toasts
            if (activeToasts.get(data.position).size() >= MAX_TOASTS_PER_POSITION) {
                applyQueueStrategy(data);
            } else {
                displayToast(data);
            }
        }

        isProcessingQueue.set(!toastQueue.isEmpty());
        if (isProcessingQueue.get()) {
            Platform.runLater(AdvancedToast::processQueue);
        }
    }

    private static void applyQueueStrategy(ToastData data) {
        switch (queueStrategy) {
            case REPLACE_ALL:
                // Close all toasts at this position and show the new one
                List<Popup> toasts = new ArrayList<>(activeToasts.get(data.position));
                for (Popup popup : toasts) {
                    popup.hide();
                }
                activeToasts.get(data.position).clear();
                positionOffsets.put(data.position, 0.0);
                displayToast(data);
                break;

            case REPLACE_SAME_TYPE:
                // Find and close toasts of the same type
                boolean found = false;
                List<Popup> sameTypeToasts = new ArrayList<>();

                for (Popup popup : activeToasts.get(data.position)) {
                    if (popup.getProperties().containsKey("toastType") &&
                            popup.getProperties().get("toastType").equals(data.type)) {
                        sameTypeToasts.add(popup);
                        found = true;
                    }
                }

                if (found) {
                    for (Popup popup : sameTypeToasts) {
                        popup.hide();
                        activeToasts.get(data.position).remove(popup);
                    }
                    recalculatePositions(data.position);
                    displayToast(data);
                } else {
                    // If no toast of same type, put back in queue
                    toastQueue.add(data);
                }
                break;

            case STACK:
            default:
                // Just put it back in the queue
                toastQueue.add(data);
                break;
        }
    }

    private static void displayToast(ToastData data) {
        try {
            // Create toast elements
            Node icon = data.customIcon != null ? data.customIcon : createIcon(data.type);

            VBox textContent = new VBox(5);
            textContent.setAlignment(Pos.CENTER_LEFT);
            textContent.getStyleClass().add("toast-content");

            if (data.title != null && !data.title.isEmpty()) {
                Label titleLabel = createTitleLabel(data.title);
                textContent.getChildren().add(titleLabel);
            }

            if (data.message != null && !data.message.isEmpty()) {
                Node messageNode;
                if (data.richText) {
                    messageNode = createRichTextMessage(data.message);
                } else {
                    messageNode = createMessageLabel(data.message);
                }
                textContent.getChildren().add(messageNode);
            }

            // Add action buttons if provided
            if (data.actions != null && !data.actions.isEmpty()) {
                HBox actionsBox = createActionsBox(data.actions, data);
                textContent.getChildren().add(actionsBox);
            }

            // Main content container
            HBox toastContainer = new HBox(10, icon, textContent);
            toastContainer.setPadding(new Insets(14, 20, 14, 20));
            toastContainer.getStyleClass().addAll("toast", data.type.name().toLowerCase());
            toastContainer.setPrefWidth(TOAST_WIDTH);
            toastContainer.setMinHeight(TOAST_MIN_HEIGHT);
            toastContainer.setAlignment(Pos.CENTER_LEFT);

            // Root container with progress bar
            StackPane rootContainer = new StackPane();

            if (data.showCloseButton && data.type != ToastType.CRITICAL) {
                // Add close button if enabled
                FontAwesomeIconView closeIcon = new FontAwesomeIconView(FontAwesomeIcon.TIMES);
                closeIcon.getStyleClass().add("toast-close-button");
                closeIcon.setSize("12");

                StackPane closeButton = new StackPane(closeIcon);
                closeButton.getStyleClass().add("toast-close-button-container");
                closeButton.setPadding(new Insets(4));
                closeButton.setCursor(Cursor.HAND);

                // Position close button in top-right corner
                StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);

                // Wrap toast container in a stack pane to position the close button
                StackPane wrapperPane = new StackPane();
                wrapperPane.getChildren().addAll(toastContainer, closeButton);
                wrapperPane.getStyleClass().add("toast-wrapper");

                rootContainer.getChildren().add(wrapperPane);

                // Configure popup
                Popup popup = new Popup();
                popup.getContent().add(rootContainer);
                popup.setAutoFix(true);
                popup.setAutoHide(false);

                // Store reference to popup for close button
                closeButton.setOnMouseClicked(event -> {
                    closeToast(popup, rootContainer, data.onClosed);
                    event.consume();
                });

                // Store toast type in popup properties for queue strategy
                popup.getProperties().put("toastType", data.type);

                // Add progress bar if not persistent
                if (!data.persistent && data.duration > 0) {
                    Rectangle progressBar = createProgressBar(data);
                    StackPane progressContainer = new StackPane(progressBar);
                    progressContainer.setAlignment(Pos.BOTTOM_CENTER);
                    rootContainer.getChildren().add(progressContainer);
                }

                // Setup click to dismiss (except for critical toasts)
                if (data.type != ToastType.CRITICAL) {
                    setupClickToDismiss(toastContainer, popup, data.onClosed);
                }

                // Show with animation
                showWithAnimation(popup, rootContainer, data);

                // Setup auto-close if not persistent
                if (!data.persistent && data.duration > 0) {
                    setupAutoClose(popup, rootContainer, data.duration, data.onClosed);
                }
            } else {
                rootContainer.getChildren().add(toastContainer);

                // Add progress bar if not persistent
                if (!data.persistent && data.duration > 0) {
                    Rectangle progressBar = createProgressBar(data);
                    StackPane progressContainer = new StackPane(progressBar);
                    progressContainer.setAlignment(Pos.BOTTOM_CENTER);
                    rootContainer.getChildren().add(progressContainer);
                }

                // Configure popup
                Popup popup = new Popup();
                popup.getContent().add(rootContainer);
                popup.setAutoFix(true);
                popup.setAutoHide(false);

                // Store toast type in popup properties for queue strategy
                popup.getProperties().put("toastType", data.type);

                // Setup click to dismiss (except for critical toasts)
                if (data.type != ToastType.CRITICAL) {
                    setupClickToDismiss(toastContainer, popup, data.onClosed);
                }


                // Show with animation
                showWithAnimation(popup, rootContainer, data);

                // Setup auto-close if not persistent
                if (!data.persistent && data.duration > 0) {
                    setupAutoClose(popup, rootContainer, data.duration, data.onClosed);
                }
            }
        } catch (Exception e) {
            System.err.println("Error displaying toast: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static Node createIcon(ToastType type) {
        FontAwesomeIconView icon = new FontAwesomeIconView(getIconForType(type));
        icon.setSize("20");
        icon.getStyleClass().addAll("toast-icon");

        // Add animation for certain toast types
        if (type == ToastType.CRITICAL) {
            PulseTransition pulse = new PulseTransition(icon);
            pulse.play();
        }

        return icon;
    }

    /**
     * Custom animation for critical icons.
     */
    private static class PulseTransition extends Transition {
        private final Node node;

        public PulseTransition(Node node) {
            this.node = node;
            setCycleDuration(Duration.seconds(1.5));
            setCycleCount(Timeline.INDEFINITE);
        }

        @Override
        protected void interpolate(double frac) {
            double scale = 1.0 + 0.2 * Math.sin(frac * 2 * Math.PI);
            node.setScaleX(scale);
            node.setScaleY(scale);

            // Also pulse the color for critical icons
            if (node instanceof FontAwesomeIconView) {
                FontAwesomeIconView icon = (FontAwesomeIconView) node;
                if (icon.getStyleClass().contains("critical")) {
                    double opacity = 0.7 + 0.3 * Math.sin(frac * 2 * Math.PI);
                    icon.setOpacity(opacity);
                }
            }
        }
    }

    private static Label createTitleLabel(String title) {
        Label label = new Label(title);
        label.getStyleClass().add("toast-title");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        label.setMaxWidth(TOAST_WIDTH - 60);
        return label;
    }

    private static Label createMessageLabel(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("toast-message");
        label.setWrapText(true);
        label.setMaxWidth(TOAST_WIDTH - 60);
        return label;
    }

    private static Node createRichTextMessage(String richText) {
        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().add("toast-rich-text");
        textFlow.setMaxWidth(TOAST_WIDTH - 60);

        // Very basic HTML-like parsing
        String[] parts = richText.split("(<[^>]*>)");
        boolean isBold = false;
        boolean isItalic = false;
        boolean isUnderline = false;

        for (String part : parts) {
            if (part.startsWith("<")) {
                // This is a tag
                String tag = part.toLowerCase();
                if (tag.equals("<b>")) isBold = true;
                else if (tag.equals("</b>")) isBold = false;
                else if (tag.equals("<i>")) isItalic = true;
                else if (tag.equals("</i>")) isItalic = false;
                else if (tag.equals("<u>")) isUnderline = true;
                else if (tag.equals("</u>")) isUnderline = false;
            } else if (!part.isEmpty()) {
                // This is text content
                Text text = new Text(part);
                text.getStyleClass().add("toast-text");

                if (isBold) text.setStyle(text.getStyle() + "-fx-font-weight: bold;");
                if (isItalic) text.setStyle(text.getStyle() + "-fx-font-style: italic;");
                if (isUnderline) text.setStyle(text.getStyle() + "-fx-underline: true;");

                textFlow.getChildren().add(text);
            }
        }

        return textFlow;
    }

    private static Rectangle createProgressBar(ToastData data) {
        Rectangle progress = new Rectangle(TOAST_WIDTH, PROGRESS_BAR_HEIGHT);
        progress.setTranslateY(TOAST_MIN_HEIGHT / 2 + 5);
        progress.getStyleClass().addAll("toast-progress", data.type.name().toLowerCase() + "-progress");

        // Progress bar animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progress.widthProperty(), TOAST_WIDTH)),
                new KeyFrame(Duration.seconds(data.duration), new KeyValue(progress.widthProperty(), 0))
        );
        timeline.setCycleCount(1);
        timeline.play();

        return progress;
    }

    private static HBox createActionsBox(List<ToastAction> actions, ToastData data) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.getStyleClass().add("toast-actions");
        box.setPadding(new Insets(8, 0, 0, 0));

        SimpleBooleanProperty actionTaken = new SimpleBooleanProperty(false);

        for (ToastAction action : actions) {
            if (action == null) continue;

            Button button = new Button(action.getText());
            button.getStyleClass().addAll("toast-action-button", getButtonStyleClass(action.getType()));

            // For critical toasts, ensure at least one button is clicked
            if (data.type == ToastType.CRITICAL) {
                button.setOnAction(event -> {
                    actionTaken.set(true);
                    if (action.getAction() != null) {
                        action.getAction().accept(button);
                    }

                    // Find the popup containing this button by traversing up
                    // We need to use the userData property to store a reference to the popup
                    Node current = button;
                    while (current != null) {
                        if (current.getUserData() instanceof Popup) {
                            Popup popup = (Popup) current.getUserData();
                            // Get the root container from the popup's content
                            if (!popup.getContent().isEmpty()) {
                                Node rootContainer = popup.getContent().get(0);
                                closeToast(popup, rootContainer, data.onClosed);
                            }
                            break;
                        }

                        if (current.getParent() != null) {
                            current = current.getParent();
                        } else {
                            break;
                        }
                    }
                });
            } else {
                button.setOnAction(event -> {
                    if (action.getAction() != null) {
                        action.getAction().accept(button);
                    }
                });
            }

            box.getChildren().add(button);
        }

        return box;
    }

    private static String getButtonStyleClass(ButtonType type) {
        switch (type) {
            case PRIMARY: return "primary-button";
            case SECONDARY: return "secondary-button";
            case SUCCESS: return "success-button";
            case DANGER: return "danger-button";
            case WARNING: return "warning-button";
            case INFO: return "info-button";
            case CONFIRM: return "confirm-button";
            case CANCEL: return "cancel-button";
            default: return "default-button";
        }
    }

    private static void setupClickToDismiss(Node toast, Popup popup, Runnable onClosed) {
        toast.setOnMouseClicked(event -> {
            // Don't close critical toasts on click
            if (popup.getProperties().containsKey("toastType") &&
                    popup.getProperties().get("toastType").equals(ToastType.CRITICAL)) {
                return;
            }

            closeToast(popup, toast, onClosed);
            event.consume();
        });
    }

    private static void showWithAnimation(Popup popup, Node toast, ToastData data) {
        Window window = data.parent.getScene().getWindow();
        double[] position = calculatePosition(window, data.position);

        // Adjust position for stacking
        position[1] += positionOffsets.get(data.position);

        popup.show(window, position[0], position[1]);
        activeToasts.get(data.position).add(popup);

        // Update offset for next toast
        double toastHeight = toast.getBoundsInLocal().getHeight() + SPACING;
        positionOffsets.put(data.position, positionOffsets.get(data.position) + toastHeight);

        // Entrance animation based on position and type
        AnimationFX animation = getEntryAnimation(data.position, data.type, toast);
        animation.setSpeed(1.5);
        animation.play();
    }

    private static AnimationFX getEntryAnimation(ToastPosition position, ToastType type, Node node) {
        // Critical toasts get special attention-grabbing animations
        if (type == ToastType.CRITICAL) {
            return new Bounce(node);
        }

        // Otherwise, animation depends on position
        switch (position) {
            case TOP_LEFT:
            case BOTTOM_LEFT:
                return new SlideInLeft(node);
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                return new SlideInRight(node);
            case TOP_CENTER:
                return new SlideInDown(node);
            case BOTTOM_CENTER:
                return new SlideInUp(node);
            default:
                return new FadeIn(node);
        }
    }

    private static double[] calculatePosition(Window window, ToastPosition position) {
        double x = 0, y = 0;

        // Get screen bounds to ensure toast is visible
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        switch (position) {
            case TOP_LEFT:
                x = window.getX() + MARGIN;
                y = window.getY() + MARGIN;
                break;
            case TOP_CENTER:
                x = window.getX() + (window.getWidth() - TOAST_WIDTH) / 2;
                y = window.getY() + MARGIN;
                break;
            case TOP_RIGHT:
                x = window.getX() + window.getWidth() - TOAST_WIDTH - MARGIN;
                y = window.getY() + MARGIN;
                break;
            case BOTTOM_LEFT:
                x = window.getX() + MARGIN;
                y = window.getY() + window.getHeight() - TOAST_MIN_HEIGHT - MARGIN;
                break;
            case BOTTOM_CENTER:
                x = window.getX() + (window.getWidth() - TOAST_WIDTH) / 2;
                y = window.getY() + window.getHeight() - TOAST_MIN_HEIGHT - MARGIN;
                break;
            case BOTTOM_RIGHT:
                x = window.getX() + window.getWidth() - TOAST_WIDTH - MARGIN;
                y = window.getY() + window.getHeight() - TOAST_MIN_HEIGHT - MARGIN;
                break;
        }

        // Ensure toast is within screen bounds
        x = Math.max(screenBounds.getMinX(), Math.min(x, screenBounds.getMaxX() - TOAST_WIDTH));
        y = Math.max(screenBounds.getMinY(), Math.min(y, screenBounds.getMaxY() - TOAST_MIN_HEIGHT));

        return new double[]{x, y};
    }

    private static void setupAutoClose(Popup popup, Node toast, int duration, Runnable onClosed) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(duration), e -> closeToast(popup, toast, onClosed))
        );
        timeline.play();
    }

    private static void closeToast(Popup popup, Node toast, Runnable onClosed) {
        // Find which position this toast belongs to
        ToastPosition position = null;
        for (Map.Entry<ToastPosition, List<Popup>> entry : activeToasts.entrySet()) {
            if (entry.getValue().contains(popup)) {
                position = entry.getKey();
                break;
            }
        }

        if (position != null) {
            final ToastPosition finalPosition = position;

            // Choose exit animation based on position
            AnimationFX animation;
            switch (finalPosition) {
                case TOP_LEFT:
                case BOTTOM_LEFT:
                    animation = new SlideOutLeft(toast);
                    break;
                case TOP_RIGHT:
                case BOTTOM_RIGHT:
                    animation = new SlideOutRight(toast);
                    break;
                case TOP_CENTER:
                    animation = new SlideOutUp(toast);
                    break;
                case BOTTOM_CENTER:
                    animation = new SlideOutDown(toast);
                    break;
                default:
                    animation = new FadeOut(toast);
            }

            animation.setSpeed(1.5);
            animation.setOnFinished(e -> {
                popup.hide();
                activeToasts.get(finalPosition).remove(popup);

                if (onClosed != null) {
                    try {
                        onClosed.run();
                    } catch (Exception ex) {
                        System.err.println("Error in onClosed callback: " + ex.getMessage());
                    }
                }

                // Recalculate positions for remaining toasts
                recalculatePositions(finalPosition);

                // Process next toast in queue
                processQueueIfNeeded();
            });
            animation.play();
        }
    }

    private static void recalculatePositions(ToastPosition position) {
        List<Popup> toasts = activeToasts.get(position);
        if (toasts.isEmpty()) {
            positionOffsets.put(position, 0.0);
            return;
        }

        // Animate each toast to its new position
        double offset = 0;
        for (Popup popup : toasts) {
            Node content = popup.getContent().get(0);
            Window window = popup.getOwnerWindow();
            double[] basePosition = calculatePosition(window, position);

            // Create animation for smooth repositioning
            double targetY = basePosition[1] + offset;

            // Use a writable property for animation
            SimpleDoubleProperty animatedY = new SimpleDoubleProperty(popup.getY());
            animatedY.addListener((obs, oldVal, newVal) -> popup.setY(newVal.doubleValue()));

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(animatedY, targetY, Interpolator.EASE_OUT)
                    )
            );
            timeline.play();

            offset += content.getBoundsInLocal().getHeight() + SPACING;
        }

        positionOffsets.put(position, offset);
    }

    private static FontAwesomeIcon getIconForType(ToastType type) {
        switch (type) {
            case SUCCESS: return FontAwesomeIcon.CHECK_CIRCLE;
            case ERROR: return FontAwesomeIcon.TIMES_CIRCLE;
            case WARNING: return FontAwesomeIcon.EXCLAMATION_TRIANGLE;
            case INFO: return FontAwesomeIcon.INFO_CIRCLE;
            case CRITICAL: return FontAwesomeIcon.EXCLAMATION_CIRCLE;
            case CUSTOM: return FontAwesomeIcon.BELL;
            default: return FontAwesomeIcon.INFO_CIRCLE;
        }
    }

    /**
     * Clear all active toast notifications.
     */
    public static void clearAllToasts() {
        toastQueue.clear();

        for (Map.Entry<ToastPosition, List<Popup>> entry : activeToasts.entrySet()) {
            List<Popup> toasts = new ArrayList<>(entry.getValue());
            for (Popup popup : toasts) {
                popup.hide();
            }
            entry.getValue().clear();
        }

        positionOffsets.replaceAll((k, v) -> 0.0);
    }
}