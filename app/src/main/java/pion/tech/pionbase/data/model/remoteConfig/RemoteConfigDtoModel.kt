package pion.tech.pionbase.data.model.remoteConfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

data class RemoteConfigDtoModel(
    val firebaseRemoteConfig: FirebaseRemoteConfig,
    val isRealData: Boolean,
)
