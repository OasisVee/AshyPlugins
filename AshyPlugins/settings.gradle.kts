rootProject.name = "AliucordPlugins"

include(
    "meow",
    "neutered"
)

rootProject.children.forEach {
    it.projectDir = file("${it.name}")
}
