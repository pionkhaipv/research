package pion.tech.pionbase.data.model.remoteConfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

data class RemoteConfigUIModel(
    val firebaseRemoteConfig: FirebaseRemoteConfig,
    val isRealData: Boolean,
)

fun RemoteConfigDtoModel.toPresentation() =
    RemoteConfigUIModel(
        firebaseRemoteConfig = this.firebaseRemoteConfig,
        isRealData = this.isRealData,
    )
