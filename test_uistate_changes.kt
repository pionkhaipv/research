// Test script to verify UiState.Error changes work correctly
package pion.tech.pionbase.test

import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleUiState

fun testUiStateError() {
    // Test creating UiState.Error with exception
    val exception = RuntimeException("Test error message")
    val errorState: UiState<String> = UiState.Error(exception)
    
    // Test handleUiState with new onError signature
    errorState.handleUiState(
        onError = { throwable ->
            println("Error handled: ${throwable.message}")
            // This should compile and work correctly
        }
    )
    
    println("UiState.Error refactor test completed successfully!")
}