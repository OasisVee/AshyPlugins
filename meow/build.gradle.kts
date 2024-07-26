version = "1.0.0"
description = "meow :3"

aliucord {
    // Changelog of your plugin
    changelog.set("""
        --
    """.trimIndent())

    excludeFromUpdaterJson.set(true)

    dependencies {
        implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    }
}
