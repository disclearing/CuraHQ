package club.curahq.core.tablist.reflection;

import java.util.Map;
import java.util.UUID;

import net.minecraft.server.v1_7_R4.EnumProtocol;

public class ReflectionConstants
{
    public static final Class<?> TAB_PACKET_CLASS;
    public static final Reflection.ConstructorInvoker TAB_PACKET_CONSTRUCTOR;
    public static final Reflection.FieldAccessor<Integer> TAB_PACKET_ACTION;
    public static final Reflection.FieldAccessor<String> TAB_PACKET_NAME;
    public static final Class<Object> GAME_PROFILE_CLASS;
    public static final Reflection.ConstructorInvoker GAME_PROFILE_CONSTRUCTOR;
    public static final Reflection.FieldAccessor<String> GAME_PROFILE_NAME;
    public static final Reflection.FieldAccessor<Object> TAB_PACKET_PROFILE;
    public static final Class<?> CRAFT_PLAYER_CLASS;
    public static final Class<?> NMS_PACKET_CLASS;
    public static final Class<?> NMS_PLAYER_CLASS;
    public static final Class<?> PLAYER_CONNECTION_CLASS;
    public static final Class<?> NETWORK_MANAGER_CLASS;
    public static final Reflection.MethodInvoker GET_HANDLE_METHOD;
    public static final Reflection.MethodInvoker GET_PROFILE_METHOD;
    public static final Reflection.MethodInvoker VERSION_METHOD;
    public static final Reflection.MethodInvoker SEND_PACKET;
    public static final Reflection.FieldAccessor<?> PLAYER_CONNECTION;
    public static final Reflection.FieldAccessor<?> NETWORK_MANAGER;
    public static final Class<?> ENUM_PROTOCOL_CLASS;
    public static final Reflection.FieldAccessor<?> ENUM_PROTOCOL_PLAY;
    public static final Reflection.FieldAccessor<Map> ENUM_PROTOCOL_REGISTRY;
    
    public static Class<Object> getUntypedClasses(final String... lookupNames) {
        EnumProtocol.class.getName();
        final int length = lookupNames.length;
        int i = 0;
        while (i < length) {
            final String lookupName = lookupNames[i];
            try {
                return Reflection.getUntypedClass(lookupName);
            }
            catch (IllegalArgumentException e) {
                ++i;
                continue;
            }
        }
        throw new IllegalArgumentException("No class found in selection given");
    }
    
    static {
        TAB_PACKET_CLASS = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo");
        TAB_PACKET_CONSTRUCTOR = Reflection.getConstructor(ReflectionConstants.TAB_PACKET_CLASS, (Class<?>[])new Class[0]);
        TAB_PACKET_ACTION = Reflection.getField(ReflectionConstants.TAB_PACKET_CLASS, Integer.TYPE, 5);
        TAB_PACKET_NAME = Reflection.getField(ReflectionConstants.TAB_PACKET_CLASS, String.class, 0);
        GAME_PROFILE_CLASS = getUntypedClasses("net.minecraft.util.com.mojang.authlib.GameProfile", "com.mojang.authlib.GameProfile");
        GAME_PROFILE_CONSTRUCTOR = Reflection.getConstructor(ReflectionConstants.GAME_PROFILE_CLASS, UUID.class, String.class);
        GAME_PROFILE_NAME = Reflection.getField(ReflectionConstants.GAME_PROFILE_CLASS, String.class, 0);
        TAB_PACKET_PROFILE = Reflection.getField(ReflectionConstants.TAB_PACKET_CLASS, ReflectionConstants.GAME_PROFILE_CLASS, 0);
        CRAFT_PLAYER_CLASS = Reflection.getCraftBukkitClass("entity.CraftPlayer");
        NMS_PACKET_CLASS = Reflection.getMinecraftClass("Packet");
        NMS_PLAYER_CLASS = Reflection.getMinecraftClass("EntityPlayer");
        PLAYER_CONNECTION_CLASS = Reflection.getMinecraftClass("PlayerConnection");
        NETWORK_MANAGER_CLASS = Reflection.getMinecraftClass("NetworkManager");
        GET_HANDLE_METHOD = Reflection.getMethod(ReflectionConstants.CRAFT_PLAYER_CLASS, "getHandle", (Class<?>[])new Class[0]);
        GET_PROFILE_METHOD = Reflection.getMethod(ReflectionConstants.CRAFT_PLAYER_CLASS, "getProfile", (Class<?>[])new Class[0]);
        VERSION_METHOD = Reflection.getMethod(ReflectionConstants.NETWORK_MANAGER_CLASS, "getVersion", (Class<?>[])new Class[0]);
        SEND_PACKET = Reflection.getMethod(ReflectionConstants.PLAYER_CONNECTION_CLASS, "sendPacket", ReflectionConstants.NMS_PACKET_CLASS);
        PLAYER_CONNECTION = Reflection.getField(ReflectionConstants.NMS_PLAYER_CLASS, ReflectionConstants.PLAYER_CONNECTION_CLASS, 0);
        NETWORK_MANAGER = Reflection.getField(ReflectionConstants.PLAYER_CONNECTION_CLASS, ReflectionConstants.NETWORK_MANAGER_CLASS, 0);
        ENUM_PROTOCOL_CLASS = Reflection.getMinecraftClass("EnumProtocol");
        ENUM_PROTOCOL_PLAY = Reflection.getField(ReflectionConstants.ENUM_PROTOCOL_CLASS, ReflectionConstants.ENUM_PROTOCOL_CLASS, 1);
        ENUM_PROTOCOL_REGISTRY = Reflection.getField(ReflectionConstants.ENUM_PROTOCOL_CLASS, Map.class, 0);
    }
}
