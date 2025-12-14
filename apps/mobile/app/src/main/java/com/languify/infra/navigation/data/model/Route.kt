package com.languify.infra.navigation.data.model

sealed class Route(val path: String) {
    data object SignIn : Route("sign-in")

    data object SignUp : Route("sign-up")

    data object Home : Route("home")
}
