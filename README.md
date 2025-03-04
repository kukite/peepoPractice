# PeepoPractice

PeepoPractice is a Fabric mod for 1.16.1 to practice splits of a Minecraft Any% speedrun. It includes things such as Bastion split, Fortress split, Stronghold split and more! It's a very lightweight and easy to use mod that can remove drastic amounts time from your average splits in real runs.

# Discord

[Join the Discord](https://discord.gg/s9m8gf6pju) to get access to pre-releases, get notified whenever a new update releases, and to see a detailed changelog for said update.

# FAQ:

**How do I make a loadout?**

```
- Select the category you want to play.
- Click "Configure" on the bottom.
- Click "Inventory" and then you can put items in.

You can also copy the inventory from another category that you've already made an inventory for by:
- Click "Copy Inventory" on the inventory config screen.
- Select the category you want to copy the inventory from.
- Click on "Copy".
```

**How do I add enchantments to items in my loadout?**

```
- Search for the enchantment you want (the book).
- Click on the enchanted book so you pick it up.
- Whilst holding the book click on the item you want to enchant.

If the item can be enchanted by the selected enchantment book it will be added to the item's enchantments.
```

**What is the "Loot Tables" tab on the inventory config screen?**

```
This is an inventory where you can view all the chest loot tables in the game.
By clicking on one of the chests it will show you all of the possible items it can have.
Press "Escape" to go back to the chests.
```

**Why can't I open the StandardSettings config screen?**
```
Because the "StandardSettings" mod is enabled.
Disable it and re-open your game.
```

# Custom Categories

## How:
- Go to `.minecraft/config/PeepoPractice/categories`. This directory will exist if you have started the game with the mod enabled. If it doesn't exist you can create it yourself.
- Create a new file with the `.json` file extension. Call it whatever you want. Preferably the ID of the category.

## Values:

These are all the values you can put in the `.json` file to configure your category.

```
"id"
TYPE -> String
DESCRIPTION -> The id of the category. No spaces accepted, only _'s. This value is used to generate the name of the category.
EXAMPLE -> "test_category"

"can_have_empty_inventory"
TYPE -> Boolean
DESCRIPTION -> This is to control the "You haven't configured an inventory" error.
EXAMPLE -> false

"hidden"
TYPE -> Boolean
DESCRIPTION -> This is to hide the category from the list.
EXAMPLE -> false

"player_properties"
CHILD VALUES ->
  "spawn_pos"
  TYPE -> Array (3 members)
  DESCRIPTION -> The first 3 values in the array are used as the xyz position for the world spawn.
  EXAMPLE -> [200, 62, -20]
  
  "spawn_angle"
  TYPE -> Array (2 members)
  DESCRIPTION -> The first 2 values in the array are used as the yaw and pitch for the world spawn angle.
  EXAMPLE -> [90, -45]
  
  "vehicle"
  TYPE -> Entity ID
  DESCRIPTION -> The entity type used for the vehicle of the player when they spawn in.
  EXAMPLE -> "minecraft:pig"
DESCRIPTION -> These are values to control the player for this category.
EXAMPLE -> {...}

"structure_properties"
CHILD VALUES ->
  TYPE -> Array
  CHILD VALUES ->
    "feature"
    TYPE -> Structure Feature ID
    DESCRIPTION -> The ID of the structure. (Same as /locate)
    EXAMPLE -> "bastion_remnant"
  
    "chunk_pos"
    TYPE -> Array (2 members)
    DESCRIPTION -> The first 2 values of the array are used as the x and z chunk coordinates of the structure.
    EXAMPLE -> [2, 5]
    
    "orientation"
    TYPE -> Direction ("north", "south", "west", "east")
    DESCRIPTION -> The orientation for the structure face in. This is only relevant for structures that do not use structure NBT files.
    EXAMPLE -> "south"
    
    "rotation"
    TYPE -> BlockRotation ("none", "clockwise_90", "clockwise_180", "counterclockwise_90")
    DESCRIPTION -> The rotation of the structure. This is only relevant for structures using structure NBT files.
    EXAMPLE -> "clockwise_90"
    
    "structure_top_y"
    TYPE -> Integer
    DESCRIPTION -> This is to control the top Y position of the structure and its children. If it is above the specified value, the structure gets translated down.
    EXAMPLE -> 50
    
    "generatable"
    TYPE -> Boolean
    DESCRIPTION -> This is to control if the structure can NATURALLY generate. This doesn't include the structure being generated if you specified "chunk_pos".
    EXAMPLE -> false
DESCRIPTION -> This is an array of structure properties.
EXAMPLE -> [{...}, {...}]

"world_properties"
CHILD VALUES ->
  "world_registry_key"
  TYPE -> World Type ("overworld", "nether", "end")
  DESCRIPTION -> This is to control the dimension that gets generated, and where the world's spawn is in.
  EXAMPLE -> "nether"
  
  "spawn_chunks_disabled"
  TYPE -> Boolean
  DESCRIPTION -> This controls whether or not spawn chunks are disabled. Spawn chunks are a set of chunks around the world spawn that are always loaded.
  EXAMPLE -> true
  
  "anti_biome_map"
  TYPE -> Array
  CHILD VALUES ->
    "biome"
    TYPE -> Biome ID
    DESCRIPTION -> The biome to not generate within the specified range. If this biome tried to generate in the nether it replaces it with "nether_wastes". In the overworld it replaces it with "plains".
    EXAMPLE -> "minecraft:desert"
    
    "range"
    TYPE -> Integer
    DESCRIPTION -> The range of which the specified biome should not be generated in. This is calculated in a circle and is centered around the world origin (0, 0). These are BLOCKS and NOT CHUNKS.
    EXAMPLE -> 200
  DESCRIPTION -> Replaces the specified biomes with another in their specified ranges.
  EXAMPLE -> [{...}, {...}]
  
  "pro_biome_map"
  TYPE -> Array
  CHILD VALUES ->
    "biome"
    TYPE -> Biome ID
    DESCRIPTION -> The biome to force generate in the specified range.
    EXAMPLE -> "minecraft:nether_wastes"
    
    "range"
    TYPE -> Integer
    DESCRIPTION -> The range of which the specified biome should be generated in. This is calculated in a circle and is centered around the world origin (0, 0). These are BLOCKS and NOT CHUNKS.
    EXAMPLE -> 200
  DESCRIPTION -> Forcibly generates the specified biomes in their specified ranges.
  EXAMPLE -> [{...}, {...}]

"split_event"
(This part will be a little bit different, as each split event has their own custom values.)
TYPE -> SplitEvent
DESCRIPTION -> This is the event that the game listens to to end the timer and save the completion.
POSSIBLE VALUES ->
  "change_dimension"
  CHILD VALUES ->
    "dimension"
    TYPE -> World Type ("overworld", "nether", "end")
    DESCRIPTION -> The NEW dimension of the player that the game should check for.
    EXAMPLE -> "end"
  DESCRIPTION -> This triggers when the player enters the specified dimension.
  EXAMPLE -> {...}
  
  "enter_vehicle"
  CHILD VALUES ->
    "vehicle"
    TYPE -> Entity Type
    DESCRIPTION -> The entity the player enters as a vehicle.
    EXAMPLE -> "minecart"
    
    "keep_item"
    TYPE -> Boolean
    DESCRIPTION -> Whether or not the player keeps the item after placing the vehicle.
    EXAMPLE -> true
  DESCRIPTION -> This triggers whenever the player enters a vehicle.
  EXAMPLE -> {...}
  
  "get_advancement"
  CHILD VALUES ->
    "advancement"
    TYPE -> Advancement Type
    DESCRIPTION -> The path of the advancement the game should check for. The paths for these can be found in the game's data files.
    EXAMPLE -> "nether/find_fortress"
  DESCRIPTION -> This triggers whenever the player gets the specified advancement.
  EXAMPLE -> {...}
  
  "interact_loot_chest"
  CHILD VALUES ->
    "loot_table"
    TYPE -> LootTable Type
    DESCRIPTION -> The path of the Loottable the game should check for. The paths for these can be found in the game's data files.
    EXAMPLE -> chests/buried_treasure
    
    "on_close"
    TYPE -> Boolean
    DESCRIPTION -> If it triggers when the chest closes. (Default is false)
  DESCRIPTION -> This triggers whenever the player opens a container attached to a block entity that has a loot table connected to it.
  EXAMPLE -> {...}
  
  "throw_entity"
  CHILD VALUES ->
    "item"
    TYPE -> Item ID
    DESCRIPTION -> The ID of the item that is thrown as a projectile entity.
    EXAMPLE -> "minecraft:ender_pearl"
  DESCRIPTION -> This triggers whenever the player throws a projectile.
  EXAMPLE -> {...}
```
