package org.singularityuniverse.console

import kotlinx.browser.window


external fun eval(arg: String): JsAny

val windowId = window.frameElement?.id