package org.tensorflow.lite.examples.videoclassification.extensions

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController


fun Fragment.navigate(
    @IdRes destination: Int,
    args: Bundle? = null,
    options: NavOptions? = null,
    navigatorExtra: Navigator.Extras? = null

) {
    catch {
        findNavController()
            .navigate(destination, args, options, navigatorExtra)
    }
}

fun Fragment.navigate(
    direction: NavDirections,
    options: NavOptions? = null
) {
    catch {
        findNavController().navigate(direction, options)
    }
}

fun Fragment.navigate(
    direction: NavDirections,
    navigatorExtra: Navigator.Extras
) {
    catch {
        findNavController().navigate(direction, navigatorExtra)
    }
}

fun Fragment.back() {
    catch {
        findNavController().popBackStack()
    }
}

fun Toolbar.back(fragment: Fragment) {
    this.setNavigationOnClickListener { fragment.back() }
}


fun View.back(fragment: Fragment) {
    setOnClickListener { fragment.back() }
}

private inline fun catch(action: () -> Unit) {
    try {
        action.invoke()
    } catch (t: Throwable) {
    }
}