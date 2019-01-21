package club.curahq.core.user;


import org.bukkit.configuration.serialization.*;

import club.curahq.core.util.core.ServerParticipator;

import java.util.*;

public class ConsoleUser extends ServerParticipator implements ConfigurationSerializable
{
    public static final UUID CONSOLE_UUID;
    String name;
    
    static {
        CONSOLE_UUID = UUID.fromString("908c0885-dfe9-4727-9fd6-1eb2890e108c");
    }
    
    public ConsoleUser() {
        super(ConsoleUser.CONSOLE_UUID);
        this.name = "CONSOLE";
    }
    
    public ConsoleUser(final Map<String, Object> map) {
        super(map);
        this.name = "CONSOLE";
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
