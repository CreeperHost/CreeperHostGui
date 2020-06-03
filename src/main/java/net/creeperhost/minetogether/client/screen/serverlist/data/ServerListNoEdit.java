package net.creeperhost.minetogether.client.screen.serverlist.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;

import java.io.File;
import java.util.ArrayList;

public class ServerListNoEdit extends ServerList
{
    private Minecraft mc;
    private ArrayList<Boolean> servers;
    
    public ServerListNoEdit(Minecraft p_i1194_1_)
    {
        super(p_i1194_1_);
    }
    
    private void prepare()
    {
        if (servers == null) servers = new ArrayList<Boolean>();
        if (mc == null) mc = Minecraft.getInstance();
    }
    
    @SuppressWarnings("Duplicates")
    @Override
    public void loadServerList()
    {
        prepare();
        super.loadServerList();
        try
        {
            this.servers.clear();
            CompoundNBT nbttagcompound = CompressedStreamTools.read(new File(this.mc.gameDir, "mtservers.dat"));
            
            byte[] serverBytes;
            
            if (nbttagcompound == null)
            {
                serverBytes = new byte[countServers()];
                for (int i = 0; i < countServers(); i++)
                {
                    serverBytes[i] = 0;
                }
            } else
            {
                serverBytes = nbttagcompound.getByteArray("servers");
            }
            
            
            int count = countServers();
            
            for (int i = 0; i < count; ++i)
            {
                this.servers.add(i < serverBytes.length && serverBytes[i] == 1);
            }
        } catch (Exception exception)
        {
            for (int i = 0; i < countServers(); i++)
            {
                servers.add(false);
            }
            //LOGGER.error((String)"Couldn\'t load server list", (Throwable)exception);
        }
    }
    
    @SuppressWarnings("Duplicates")
    @Override
    public void saveServerList()
    {
        prepare();
        super.saveServerList();
        int numOfServers = countServers();
        byte[] serverBytes = new byte[numOfServers];
        
        for (int i = 0; i < numOfServers; i++)
        {
            boolean editStatus = i < servers.size() && servers.get(i);
            serverBytes[i] = editStatus ? (byte) 1 : (byte) 0;
        }
        
        try
        {
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putByteArray("servers", serverBytes);
            CompressedStreamTools.safeWrite(nbttagcompound, new File(this.mc.gameDir, "mtservers.dat"));
        } catch (Exception exception)
        {
            //logger.error("Couldn\'t save server list", exception);
        }
    }
    
    @Override
    public void func_217506_a(ServerData p_78851_1_)
    {
        servers.remove(p_78851_1_);
        super.func_217506_a(p_78851_1_);
    }
    
    @Override
    public void addServerData(ServerData p_78849_1_)
    {
        servers.add(p_78849_1_ instanceof ServerDataPublic);
        super.addServerData(p_78849_1_);
    }
    
    public boolean isLocked(int place)
    {
        return servers.size() > place && servers.get(place);
    }
}
