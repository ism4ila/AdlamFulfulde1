package com.bekisma.adlamfulfulde.di

import com.bekisma.adlamfulfulde.ads.AdMobManager
import com.bekisma.adlamfulfulde.ads.SimpleInterstitialManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdModule {
    
    @Provides
    @Singleton
    fun provideAdMobManager(): AdMobManager {
        return AdMobManager
    }
    
    @Provides
    @Singleton
    fun provideSimpleInterstitialManager(): SimpleInterstitialManager {
        return SimpleInterstitialManager
    }
}