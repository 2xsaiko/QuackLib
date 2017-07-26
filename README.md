# QuackLib

A collection of useful tools for programming Minecraft mods in Kotlin, most notably:
 - QBlock, a way to combine Block and TileEntity classes into one class while eliminating the communication code between those two.
 - A wrapper for NBT tags to use Kotlin-specific features
 - A GUI API in which you write your GUI definitions as .json files and the logic class seperately, allowing resource pack makers and players to easily change the look of the GUI if they want to.

All of the features listed here are used in [RetroComputers](https://gitlab.com/the_real_farfetchd/RetroComputers).

If you want to use QuackLib in your own projects, you can add this to your build.gradle file:

    repositories {
        maven {
            url "http://farfetchd.duckdns.org/maven"
        }
    }
    
    dependencies {
        deobfCompile "quacklib:quacklib-exp:1.0.0_0"
    }

All versions (in this case 1.0.0_0) are listed [here](http://farfetchd.duckdns.org/maven/quacklib/quacklib-exp/).