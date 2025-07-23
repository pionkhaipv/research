Project Structure:
Follow Google’s recommended app architecture, using only the UI Layer and Data Layer, skipping the Domain Layer.
Always use a Repository with Hilt for data fetching and handling.

Model Separation by Layer:

The Data Layer uses DTO classes (e.g., TemplateDtoModel),

The UI Layer uses UI models (e.g., TemplateUiModel).

Use a toPresentation extension function to map TemplateDtoModel to TemplateUiModel.

This function should be written as an extension function in the same file as TemplateDtoModel.

Each screen must have 3 main components:

A Fragment file (which holds the screen),

A corresponding FragmentEx file (which contains separated logic like click handling, init, setting listeners – refer to HomeFragment, HomeFragmentEx),

The corresponding ViewModel.

When using components like BottomSheet, DialogFragment, Fragment, Adapter, or ViewModel, always use the corresponding base classes:

BaseBottomSheetDialogFragment

BaseDialogFragment

BaseFragment

BaseListAdapter

BaseViewModel

Additional Notes:

No need to write code for testing.

No need to build the project before submitting results.

Code must be easy to read and easy to maintain.

Follow SOLID principles as much as possible — if applying SOLID makes the code too verbose, it can be skipped to save development time.

Comments should be short and easy to understand, and written in English.