Project Structure:
Follow Google’s recommended app architecture, using only the UI Layer and Data Layer, skipping the Domain Layer.
Always use a Repository with Hilt for data fetching and handling.

No need to write code for testing.

No need to build the project before submitting results.

No need to write code for testing.

Model Separation by Layer:

The Data Layer uses DTO classes (e.g., TemplateDtoModel).

The UI Layer uses UI models (e.g., TemplateUiModel).

Use a toPresentation extension function to map TemplateDtoModel to TemplateUiModel.

This function should be written as an extension function in the same file as TemplateDtoModel.

Repository Rule:
All data-fetching functions in the Repository must return Flow<Result<TypeOfDataNeeded>>.

Each screen must have 3 main components:

A Fragment file (defines the UI),

A corresponding FragmentEx file (handles separated logic such as click listeners, init logic – see HomeFragment, HomeFragmentEx as reference),

The corresponding ViewModel.

When using the following components, always extend from the corresponding base classes:

BaseBottomSheetDialogFragment

BaseDialogFragment

BaseFragment

BaseListAdapter

BaseViewModel

Additional Notes:

Code must be easy to read and easy to maintain.

Follow SOLID principles as much as possible. If applying SOLID causes the code to become unnecessarily verbose, you can skip it to save development time.

Comments should be short, clear, and written in English.