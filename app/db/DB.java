package db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import models.DcsEntity;
import models.DcsObject;
import models.DcsPlayer;

public class DB
{
	private static ConcurrentMap<Long, DcsObject> objects = new ConcurrentHashMap<>();
	private static ConcurrentMap<Long, DcsObject> oldObjects = new ConcurrentHashMap<>();
	private static ConcurrentMap<Long, DcsObject> activeObjects = new ConcurrentHashMap<>();
	
	private static List<DcsPlayer> players = new ArrayList<>();
	
	private static double latestObjectTime = -1L;
	private static double latestPlayerTime = -1L;
	
	
	public static Collection<DcsObject> getObjects()
	{
		return objects.values();
	}
	
	public static Collection<DcsObject> getActiveObjects()
	{
		return activeObjects.values();
	}

	public static Collection<DcsPlayer> getPlayers()
	{
		return players;
	}

	private static boolean isActive(DcsObject obj)
	{
		DcsObject old = oldObjects.get(obj.id);
		if (old == null)
			return false;
		if (obj.heading != old.heading || obj.lat != old.lat || obj.lon != old.lon)
			return true;
		return false;
	}

	public static void updateEntity(DcsEntity entity)
	{
		if (entity instanceof DcsPlayer)
			updatePlayer((DcsPlayer) entity);
		else if (entity instanceof DcsObject)
			updateObject((DcsObject) entity);
	}
	
	public static void updatePlayer(DcsPlayer player)
	{
		if (player == null)
    		return;
    	if (player.time > latestPlayerTime)
    	{
    		players = new ArrayList<>();
    		latestPlayerTime = player.time;
    	}
    	players.add(player);
	}

	public static void updateObject(DcsObject object)
	{
    	if (object == null)
    		return;
    	if (object.time > latestObjectTime)
    	{
    		oldObjects = objects;
    		objects = new ConcurrentHashMap<>();
    		activeObjects.clear();
    		latestObjectTime = object.time;
    	}
    	objects.put(object.id, object);
    	if (isActive(object))
    		activeObjects.put(object.id, object);
		
	}
	
	public static void clear()
	{
		objects.clear();
		activeObjects.clear();
		oldObjects.clear();
		players.clear();
	}
	
}
