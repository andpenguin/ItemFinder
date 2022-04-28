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

public class Main {
    // Looking through a seed randomly
    private static Item item;
    private static BPos itemLoc;
    private static LinkedHashMap<Integer, Item> items;
    private static final ChunkRand rand = new ChunkRand();
    private static final MCVersion VERSION = MCVersion.v1_16_1;
    private static final RuinedPortal portal = new RuinedPortal(Dimension.OVERWORLD, VERSION);
    private static final DesertPyramid temple = new DesertPyramid(VERSION);
    private static final BuriedTreasure treasure = new BuriedTreasure(VERSION);
    private static final Shipwreck shipwreck = new Shipwreck(VERSION);
    private static final EndCity city = new EndCity(VERSION);

    public static void main (String[] args) {
        WindowFrame window = new WindowFrame();
        window.display();
        items = Items.getItems();
    }

    public static String[] getEnchantNames() {
        List<Enchantment> enchantList = new ArrayList<>();
        Enchantments.apply(enchantList, VERSION);
        String[] nameList = new String[enchantList.size()+1];
        nameList[0] = "none";
        for (int i = 1; i < enchantList.size()+1; i++)
            nameList[i] = enchantList.get(i-1).getName();
        return nameList;
    }

    public static ArrayList<Pair<String,Integer>> getEnchantByName(String name, int level) {
        System.out.println(level);
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

    public static Item getItemByName(String name) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(name))
                item = items.get(i);
        }
        return null;
    }

    public static String findItemOnSeed(long seed, ArrayList<Pair<String, Integer>> enchants) {
        System.out.println("Locating the item...");
        item.setEnchantments(enchants);
        BPos loc = findClosestItem(seed);
        return "X: " + loc.getX() + " Z: " + loc.getZ();
    }

    public static BPos findClosestItem(long seed) {
        int x = -1;
        int z = -1;
        int steps = 0;
        int sideLength = 1;
        int position = 0;
        int turns = 0;
        String[] directions = new String[] {"right", "down", "left", "up"};
        while (!findItem(seed, x, z)) {
            if (steps == sideLength)
            {
                position++;
                turns++;
                if (position == directions.length)
                    position = 0;
                if (turns % 2 == 0)
                    sideLength++;
                steps = 0;
            }
            switch (directions[position]) {
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
            steps++;
        }
        return itemLoc;
    }

    public static boolean findItem(long seed, int x, int z) {
        boolean inPortal = false;
        boolean inTemple = false;
        boolean inTreasure = false;
        boolean inShip = false;
        boolean inCity = false;
        if (new RuinedPortalGenerator(VERSION).getPossibleLootItems().contains(item))
            inPortal  = findInStructure(portal, new RuinedPortalGenerator(VERSION), seed, x, z);
        if (new DesertPyramidGenerator(VERSION).getPossibleLootItems().contains(item))
            inTemple = findInStructure(temple, new DesertPyramidGenerator(VERSION), seed, x, z);
        if (new BuriedTreasureGenerator(VERSION).getPossibleLootItems().contains(item))
            inTreasure = findInStructure(treasure, new BuriedTreasureGenerator(VERSION), seed, x, z);
        if (new ShipwreckGenerator(VERSION).getPossibleLootItems().contains(item))
            inShip = findInStructure(shipwreck, new ShipwreckGenerator(VERSION), seed, x, z);
        if (new EndCityGenerator(VERSION).getPossibleLootItems().contains(item))
            inCity = findInEndCity(city, new EndCityGenerator(VERSION), seed, x, z);
        return inPortal || inTemple || inTreasure || inShip || inCity;
    }

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
            if (contains(chest, item)) {
                if (itemLoc == null || itemLoc.getMagnitudeSq() > structureLoc.toBlockPos().getMagnitudeSq())
                    itemLoc = structureLoc.toBlockPos();
                return true;
            }
        return false;
    }

    public static boolean findInEndCity(EndCity city, EndCityGenerator gen, long seed, int x, int z) {
        CPos structureLoc = city.getInRegion(seed, x, z, rand);
        EndBiomeSource source = new EndBiomeSource(VERSION, seed);
        if (structureLoc == null)
            return false;
        if (!city.canSpawn(structureLoc, source))
            return false;
        if (!gen.generate(new EndTerrainGenerator(source), structureLoc))
            return false;
        List<ChestContent> loot = city.getLoot(seed, gen,false);
        for (ChestContent chest: loot)
            if (contains(chest, item)) {
                itemLoc = structureLoc.toBlockPos();
                return true;
            }
        return false;
    }

    public static boolean contains(ChestContent chest, Item item) {
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
