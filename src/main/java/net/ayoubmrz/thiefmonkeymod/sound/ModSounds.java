package net.ayoubmrz.thiefmonkeymod.sound;


import net.ayoubmrz.thiefmonkeymod.ThiefMonkeyMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent MONKEY_HURT = registerSoundEvent("monkey_hurt");
    public static final SoundEvent MONKEY_HURT2 = registerSoundEvent("monkey_hurt2");

    public static final SoundEvent MONKEY_AMBIENT1 = registerSoundEvent("monkey_ambient1");
    public static final SoundEvent MONKEY_AMBIENT2 = registerSoundEvent("monkey_ambient2");
    public static final SoundEvent MONKEY_AMBIENT3 = registerSoundEvent("monkey_ambient3");
    public static final SoundEvent MONKEY_AMBIENT4 = registerSoundEvent("monkey_ambient4");

    public static final SoundEvent MONKEY_DEATH1 = registerSoundEvent("monkey_death1");
    public static final SoundEvent MONKEY_DEATH2 = registerSoundEvent("monkey_death2");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(ThiefMonkeyMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        ThiefMonkeyMod.LOGGER.info("Registering Mod Sounds for " + ThiefMonkeyMod.MOD_ID);
    }
}
