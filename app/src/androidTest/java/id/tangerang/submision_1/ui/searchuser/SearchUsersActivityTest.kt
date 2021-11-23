package id.tangerang.submision_1.ui.searchuser

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.android.material.tabs.TabLayout
import id.tangerang.submision_1.R
import junit.framework.TestCase
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SearchUsersActivityTest : TestCase(){
    private val dummyName = "sidiqpermana"

    @Before
    fun setup(){
        ActivityScenario.launch(SearchUsersActivity::class.java)
    }

    @Test
    fun assertSearchUsers() {
        onView(withId(R.id.etCari)).perform(typeText(dummyName), closeSoftKeyboard())
        onView(withId(R.id.etCari)).perform(pressImeActionButton())
        Thread.sleep(2000)
        onView(withId(R.id.rvList)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        Thread.sleep(1000)
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1))
        Thread.sleep(1000)
        onView(withId(R.id.btnFavorite)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.btnBack)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.menu_favorite)).perform(click())
        Thread.sleep(3000)
    }

    private fun selectTabAtPosition(indextTab: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription() = "with tab at index $indextTab"

            override fun getConstraints() = allOf(isDisplayed(), isAssignableFrom(TabLayout::class.java))

            override fun perform(uiController: UiController, view: View) {
                val tabLayout = view as TabLayout
                val tabAtIndex: TabLayout.Tab = tabLayout.getTabAt(indextTab)
                    ?: throw PerformException.Builder()
                        .withCause(Throwable("No tab at index $indextTab"))
                        .build()
                tabAtIndex.select()
            }
        }
    }
}