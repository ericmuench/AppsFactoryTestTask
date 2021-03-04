package de.ericmuench.appsfactorytesttask.ui.launch

import android.os.Bundle
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.ui.main.MainActivity
import de.ericmuench.appsfactorytesttask.ui.uicomponents.abstract_activities_fragments.BaseActivity
import gr.net.maroulis.library.EasySplashScreen


class LaunchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //create splash screen + configure it
        val typedValueBackground = TypedValue()
        theme.resolveAttribute(R.attr.colorSecondary, typedValueBackground, true)
        val colorBackground = typedValueBackground.data

        val splashScreen = EasySplashScreen(this)
            .withFullScreen()
            .withTargetActivity(MainActivity::class.java)
            .withSplashTimeOut(1000)
            .withBackgroundColor(colorBackground)
            .withLogo(R.drawable.ic_album_splash)
            .withAfterLogoText(resources.getString(R.string.by_author))
            .withBeforeLogoText(resources.getString(R.string.app_name)).apply {
                afterLogoTextView.setPadding(0,16,0,0)
                beforeLogoTextView.setPadding(0,0,0,16)
            }


        setContentView(splashScreen.create())
    }
}