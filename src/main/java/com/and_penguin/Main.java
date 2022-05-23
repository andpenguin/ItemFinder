package com.and_penguin;

import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.loot.ChestContent;
import kaptainwutax.featureutils.loot.ILoot;
import kaptainwutax.featureutils.loot.enchantment.Enchantment;
import kaptainwutax.featureutils.loot.enchantment.Enchantments;
import kaptainwutax.featureutils.loot.item.Item;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.loot.item.Items;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.featureutils.structure.generator.Generator;
import kaptainwutax.featureutils.structure.generator.structure.*;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.terrainutils.terrain.EndTerrainGenerator;
import kaptainwutax.terrainutils.terrain.OverworldTerrainGenerator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The Main class manages the searching in the background of the application.
 * It saves an Item class from a list of all items, sends an enchantment list to the application,
 * and then loops through a spiral pattern for the item.
 *
 * @author and_penguin
 * @author libaries created by Neil helped simulate chest loot and structure positions
 */
public class Main {
    private static Item item; // The item to be searched for
    private static BPos itemLoc; // The closest location the item was found
    private static LinkedHashMap<Integer, Item> items; // The list of all possible items
    private static final ChunkRand rand = new ChunkRand(); // Used to simulate Java Random
    private static final MCVersion VERSION = MCVersion.v1_16_1; // Version of the game to search on
    // Different structures to search in
    private static final RuinedPortal portal = new RuinedPortal(Dimension.OVERWORLD, VERSION);
    private static final DesertPyramid temple = new DesertPyramid(VERSION);
    private static final BuriedTreasure treasure = new BuriedTreasure(VERSION);
    private static final Shipwreck shipwreck = new Shipwreck(VERSION);
    private static final EndCity city = new EndCity(VERSION);

    /**
     * Creates the application window and initializes the list of items
     * @param args arguments from the command line
     */
    public static void main (String[] args) {
        WindowFrame window = new WindowFrame();
        window.display();
        items = Items.getItems();
    }

    /**
     * Creates an array of all possible enchantment names to
     * be used in the dropdown menu
     * @return the list of possible enchant names
     */
    public static String[] getEnchantNames() {
        List<Enchantment> enchantList = new ArrayList<>();
        Enchantments.apply(enchantList, VERSION);
        String[] nameList = new String[enchantList.size()+1];
        nameList[0] = "none";
        for (int i = 1; i < enchantList.size()+1; i++)
            nameList[i] = enchantList.get(i-1).getName();
        return nameList;
    }

    /**
     * Returns a list of enchantments (Pairs of String names and Integer levels)
     * that match a certain string name and int level
     * @param name The name of the enchantments to be found
     * @param level the level of the enchantments to be found
     * @return a list of enchantments (always size of 1) that match the parameters
     */
    public static ArrayList<Pair<String,Integer>> getEnchantByName(String name, int level) {
        List<Enchantment> enchantList = new ArrayList<>();
        Enchantments.apply(enchantList, VERSION);
        ArrayList<Pair<String,Integer>> enchants = new ArrayList<>();
        for (int i = 0; i < enchantList.size(); i++) {
            String enchantName = enchantList.get(i).getName();
            if (enchantName.equals(name)) {
                enchants.add(new Pair<>(name, level));
                return enchants;
            }
        }
        return null;
    }

    /**
     * Initializes the item instance variable given an item name
     *
     * @param name the technical name for an item - eg: enchanted_golden_apple
     * @return true if the name succesfully matched the name of an item, otherwise,
     *         false
     */
    public static boolean setItemByName(String name) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(name)) {
                item = items.get(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Finds information about the location of the closest of an item on a given seed
     * @param seed the seed to search for the item on
     * @param enchants enchants to be added to the item
     * @return Information about the seed and location of the item
     */
    public static String findItemOnSeed(long seed, ArrayList<Pair<String, Integer>> enchants) {
        item.setEnchantments(enchants); // Add the enchantments
        findClosestItem(seed); // Find the item's location
        if (itemLoc != null) { // If an item was found
            int x = itemLoc.getX();
            int z = itemLoc.getZ();
            itemLoc = null; // Reset the item's position for future uses
            return "Seed: " + seed + " - Location: X: " + x + " Z: " + z;
        }
        return "Seed: " + seed +  " - Could not find that item within a reasonable distance";
    }

    /**
     * Finds the actual location of the closest item on a given seed
     * The method searches in a spiral pattern, checking for the item at each point on the spiral
     * @param seed the seed to search on
     * @return the Block position of the chest where the item is (accurate to a +- 8 blocks)
     */
    public static void findClosestItem(long seed) {
        // Initial spiral position
        int x = -1;
        int z = -1;
        int steps = 0;
        int sideLength = 1;
        int position = 0;
        int turns = 0;
        // Spiral directions
        String[] directions = new String[] {"right", "down", "left", "up"};
        while (!findItem(seed, x, z)) { // While still searching
            if (steps == sideLength) // If at a turn
            {
                position++; // change direction
                turns++; // Add to turns counter
                if (position == directions.length) // if you go over the array length
                    position = 0;
                if (turns % 2 == 0) // If side length needs to be extended
                    sideLength++;
                steps = 0; // Reset steps counter
            }
            switch (directions[position]) { // Find which direction to change
                case "right": x++;
                    break;
                case "down": z++;
                    break;
                case "left": x--;
                    break;
                case "up": z--;
                    break;
                default: break;
            }
            steps++; // Increase steps counter
            if (x >= 20 || z >= 20) // If item is unreasonably far out (likely not a real item)
                return;
        }
    }

    /**
     * Finds if the item is in a given structure region
     * @param seed the seed to search on
     * @param x the x coordinate of the given structure region
     * @param z the z coordinate of the given structure region
     * @return true if the item is in any of the valid structures in the (x,z)
     *         structure region, otherwise,
     *         false
     */
    public static boolean findItem(long seed, int x, int z) {
        boolean inPortal = false;
        boolean inTemple = false;
        boolean inTreasure = false;
        boolean inShip = false;
        boolean inCity = false;
        //ruined portal check
        if (new RuinedPortalGenerator(VERSION).getPossibleLootItems().contains(item))
            inPortal  = findInStructure(portal, new RuinedPortalGenerator(VERSION), seed, x, z);
        //desert temple check
        if (new DesertPyramidGenerator(VERSION).getPossibleLootItems().contains(item))
            inTemple = findInStructure(temple, new DesertPyramidGenerator(VERSION), seed, x, z);
        // buried treasure check
        if (new BuriedTreasureGenerator(VERSION).getPossibleLootItems().contains(item))
            inTreasure = findInStructure(treasure, new BuriedTreasureGenerator(VERSION), seed, x, z);
        // shipwreck check
        if (new ShipwreckGenerator(VERSION).getPossibleLootItems().contains(item))
            inShip = findInStructure(shipwreck, new ShipwreckGenerator(VERSION), seed, x, z);
        // end city check
        if (new EndCityGenerator(VERSION).getPossibleLootItems().contains(item))
            inCity = findInEndCity(city, new EndCityGenerator(VERSION), seed, x, z);
        // Check if any of the structures are true
        return inPortal || inTemple || inTreasure || inShip || inCity;
    }

    /**
     * Checks if the item can be found in a specific structure of a given region
     * @param structure a subclass of the RegionStructure that can be checked for loot
     * @param gen the Generator that matches the region structure
     * @param seed the seed to search on
     * @param x the x coordinate of the region
     * @param z the z coordinate of teh region
     * @return true if the structure contains the item, otherwise,
     *         false
     */
    public static boolean findInStructure(RegionStructure structure, Generator gen, long seed, int x, int z) {
        CPos structureLoc = structure.getInRegion(seed, x, z, rand);
        OverworldBiomeSource source = new OverworldBiomeSource(VERSION, seed);
        if (structureLoc == null)
            return false;
        if (!structure.canSpawn(structureLoc, source))
            return false;
        if (!gen.generate(new OverworldTerrainGenerator(source), structureLoc))
            return false;
        ILoot lootChecker = (ILoot) structure;
        List<ChestContent> loot = lootChecker.getLoot(seed, gen,false);
        for (ChestContent chest: loot)
            if (contains(chest)) {
                if (itemLoc == null || itemLoc.getMagnitudeSq() > structureLoc.toBlockPos().getMagnitudeSq())
                    itemLoc = structureLoc.toBlockPos();
                return true;
            }
        return false;
    }

    /**
     * Seperate method similar to findInStructure for end cities
     * because they are in a different dimension
     * @param city the EndCity to check in
     * @param gen the EndCity's generator to check for loot with
     * @param seed the seed to check on
     * @param x the x region to check in
     * @param z the z region to check in
     * @return
     */
    public static boolean findInEndCity(EndCity city, EndCityGenerator gen, long seed, int x, int z) {
        CPos structureLoc = city.getInRegion(seed, x, z, rand);
        if (structureLoc == null)
            return false;
        EndBiomeSource source = new EndBiomeSource(VERSION, seed);
        if (!city.canSpawn(structureLoc, source))
            return false;
        if (!gen.generate(new EndTerrainGenerator(source), structureLoc))
            return false;
        List<ChestContent> loot = city.getLoot(seed, gen,false);
        for (ChestContent chest: loot)
            if (contains(chest)) {
                itemLoc = structureLoc.toBlockPos();
                return true;
            }
        return false;
    }

    /**
     * Checks if a given chest has the item
     * If the item has no enchantments, default to the standard contains() method,
     * otherwise, check if the item exists without enchants and then loop through the enchants
     * @param chest the chest object to search for the item
     * @return
     */
    public static boolean contains(ChestContent chest) {
        if (item.getEnchantments().size() == 0)
            return chest.contains(item);
        Pair<String, Integer> enchantment = item.getEnchantments().get(0);
        for (ItemStack stack: chest.getItems()) {
            List<Pair<String, Integer>> otherEnchants = stack.getItem().getEnchantments();
            if (item.getName().equals(stack.getItem().getName())) {
                for (Pair<String, Integer> enchant : otherEnchants)
                    if (enchant.equals(enchantment))
                        return true;
            }
        }
        return false;
    }
}
