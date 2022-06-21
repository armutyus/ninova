package com.armutyus.ninova.fragmentfactory

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface NinovaFragmentFactoryEntryPoint {
    fun getFragmentFactory(): NinovaFragmentFactory
}