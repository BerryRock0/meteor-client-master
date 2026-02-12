package meteordevelopment.meteorclient.systems.modules.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class TickManipulator extends Module
{
    private final SettingGroup sgClient = settings.createGroup("Client");
    private final SettingGroup sgServer = settings.createGroup("Server");

    public final Setting beginclientdelay = sgClient.add(new IntSetting.Builder().name("begin-client-delay").description("client timer begin value.").defaultValue(0).build());
    public final Setting endclientdelay = sgClient.add(new IntSetting.Builder().name("end-client-delay").description("client timer begin value.").defaultValue(0).build());
    public final Setting clientbool = sgClient.add(new BoolSetting.Builder().name("client-bool").description("update client boolean.").defaultValue(false).build());
    public final Setting clientincrement = sgClient.add(new BoolSetting.Builder().name("client-increment").description("increment server time value.").defaultValue(false).build());
    public final Setting clientdecrement = sgClient.add(new BoolSetting.Builder().name("client-decrement").description("decrement server time value.").defaultValue(false).build());
    public final Setting clientqueue = sgClient.add(new BoolSetting.Builder().name("client-queue").description("in queue returning boolean value.").defaultValue(false).build());
    public final Setting clientalways = sgClient.add(new BoolSetting.Builder().name("client-always").description("in iterate returning boolean value.").defaultValue(false).build());

    public final Setting beginserverdelay = sgServer.add(new IntSetting.Builder().name("begin-server-delay").description("server timer begin value.").defaultValue(0).build());
    public final Setting endserverdelay = sgServer.add(new IntSetting.Builder().name("end-server-delay").description("server timer begin value.").defaultValue(0).build());
	public final Setting serverbool = sgServer.add(new BoolSetting.Builder().name("server-bool").description("update server boolean.").defaultValue(false).build());
    public final Setting serverincrement = sgServer.add(new BoolSetting.Builder().name("server-increment").description("increment server time value.").defaultValue(false).build());
    public final Setting serverdecrement = sgServer.add(new BoolSetting.Builder().name("server-decrement").description("decrement server time value.").defaultValue(false).build());
    public final Setting serverqueue = sgServer.add(new BoolSetting.Builder().name("server-queue").description("queue returning boolean value.").defaultValue(false).build());
    public final Setting serveralways = sgServer.add(new BoolSetting.Builder().name("server-always").description("always returning boolean value.").defaultValue(false).build());
    
	public int clientTimer;
    public int serverTimer;

    public TickManipulator()
    {
        super(Categories.Misc, "tick-manipulator", "Manipulates world ticks");
    }

	public boolean clientTime()
    {
        // wait for timer
		if(clientTimer != (int)endclientdelay.get() && Boolean.TRUE.equals(clientbool.get()))
		{
			if ((Boolean)clientincrement.get()) clientTimer--;
            if ((Boolean)clientdecrement.get()) clientTimer++;
			return (Boolean)clientqueue.get();
		}
		clientTimer = (Integer)beginclientdelay.get();
        return (Boolean)clientalways.get();
    }

    public boolean serverTime()
    {
        // wait for timer
		if(serverTimer != (int)endserverdelay.get() && Boolean.TRUE.equals(serverbool.get()))
		{
			if ((Boolean)serverincrement.get()) serverTimer--;
            if ((Boolean)serverdecrement.get()) serverTimer++;
			return (Boolean)serverqueue.get();
		}
		serverTimer = (Integer)beginserverdelay.get();
        return (Boolean)serveralways.get();
    }
    
}
