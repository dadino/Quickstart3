package com.dadino.quickstart3.core.entities

/**
 * Represents a side effect that can be triggered by a state change in a ViewModel or similar reactive component.
 *
 * Side effects are typically used to represent actions that are not directly related to the state of the UI, but rather
 * actions that should be performed in response to state changes, such as navigation, displaying dialogs,
 * showing snackbars, triggering analytics events, launching network requests, or interacting with resources in the background.
 *
 * In the context of QuickLoops, SideEffects are used to start [SideEffectHandler] instances, which perform operations
 * such as loading or saving resources, or any other kind of background task that should be executed in response
 * to a change in application state.
 *
 * Side effects are distinct from UI states in that they don't directly affect the rendered UI. They are typically
 * one-time events or actions that are triggered and then consumed.  This helps maintain a clear separation
 * between UI state (what is displayed) and actions performed in response to that state.
 *
 * Common Use Cases in QuickLoops:
 *  - **Resource Loading/Saving:** Triggering the load or save of data to/from persistent storage or remote sources.
 *  - **Background Operations:** Initiating long-running tasks that do not directly update the UI, such as complex calculations.
 *  - **Navigation:** Triggering navigation to a different screen.
 *  - **UI Feedback:** Showing snackbars or toasts.
 *  - **Dialogs:** Displaying alert or confirmation dialogs.
 *  - **External Actions:** Launching activities or opening URLs.
 *  - **Analytics:** Logging events to an analytics service.
 *  - **Data Management:**  Triggering data refreshes or uploads.
 *
 *  **Important Considerations:**
 *   - **One-time Events:** Side effects are generally designed to be consumed once.  Implementations using this
 *     base class should ensure that they are handled only once to avoid unintended consequences.
 *   - **Immutability:** Side effect instances should ideally be immutable to ensure predictable behavior.
 *   - **Observability:**  A mechanism (e.g., a `Flow` or `LiveData`) is typically used to observe and react to side effects.
 *   - **Side Effect Handler:**  A corresponding handler (e.g., a [SideEffectHandler]) is needed to consume */
open class SideEffect