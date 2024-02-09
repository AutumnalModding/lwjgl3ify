package me.eigenraven.lwjgl3ify.relauncher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.apache.logging.log4j.LogManager;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.FMLLaunchHandler;

public class Lwjgl3ifyRelauncherTweaker implements ITweaker {

    @SuppressWarnings("unchecked")
    public Lwjgl3ifyRelauncherTweaker() throws ReflectiveOperationException, MalformedURLException, URISyntaxException {
        URL mySrc = Lwjgl3ifyRelauncherTweaker.class.getProtectionDomain()
            .getCodeSource()
            .getLocation();
        while (mySrc.getProtocol()
            .equals("jar")) {
            final String all = mySrc.toString();
            mySrc = new URL(all.substring("jar:".length(), all.lastIndexOf('!')));
        }
        File myFile = new File(".");
        if (mySrc.getProtocol()
            .equals("file")) {
            myFile = Paths.get(myFile.toURI())
                .toFile();
        }
        // If this tweaker is loaded, FML skips loading the coremod by default.
        Launch.classLoader.addTransformerExclusion("me.eigenraven.lwjgl3ify.core.Lwjgl3ifyCoremod");
        final Class<CoreModManager> cmmClass = (Class<CoreModManager>) Class
            .forName("cpw.mods.fml.relauncher.CoreModManager");
        final Method cmmAddCoremod = cmmClass
            .getDeclaredMethod("loadCoreMod", LaunchClassLoader.class, String.class, File.class);
        cmmAddCoremod.setAccessible(true);
        cmmAddCoremod.invoke(null, Launch.classLoader, "me.eigenraven.lwjgl3ify.core.Lwjgl3ifyCoremod", myFile);
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        if (Launch.blackboard.get("lwjgl3ify:rfb-booted") != Boolean.TRUE) {
            if (FMLLaunchHandler.side()
                .isServer()) {
                LogManager.getLogger()
                    .fatal(
                        "lwjgl3ify relauncher does not support server launches, install according to lwjgl3ify's README.");
                FMLCommonHandler.instance()
                    .exitJava(1, false);
            }
            // Not launched via RFB, transfer control to the relauncher.
            try {
                new Relauncher(args.toArray(new String[0]), profile).run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {}

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
