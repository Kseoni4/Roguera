package com.rogurea.main;

public class View {

    static String[] ActionNames = {
            "Go to the next room",
            "Go back",
            "Check inventory",
            "Go to the shop",
            "Buy",
            "Exit",
            "Attack",
            "Defence",
            "Escape"
    };

    enum Actions {
        NEXTROOM,
        BACK,
        CHECHINV,
        GOSHOP,
        BUY,
        EXIT,
        ATK,
        DEF,
        ESC
    }

    static Actions[] actions;

    static StringBuilder OutString = new StringBuilder();

    public static void Print(Player p, Room r, GameLoop.Options options){
        System.out.print(PlayerInfo(p));
        switch (options){
            case ActionMenu -> System.out.print(ActionMenuOut(r));
            case FightMenu -> System.out.print(FightMenuOut(r));
            case TargetMenu -> System.out.print(TargetMenuOut(r));
        }

    }

    public static String PlayerInfo(Player p){
        return          "Player "+ p.nickName  + " \n" +
                        "\t HP: "+ p.HP + " MP: " + p.MP +
                        " Room: " + p.CurrentRoom + "\n";
    }

    public static String ActionMenuOut(Room r){

        OutString = new StringBuilder();

        actions = new Actions[Actions.values().length];

        boolean endLine = false;
        int Index = 0;
        int i = Index;

        while(!endLine){
            Index++;
            i = 0;
            OutString.append(" [").append(Index).append("] ");
            if(!r.IsEndRoom && Index < 2){
                Append(i, Index);
                continue;
            }
            i++;
            if(r.NumberOfRoom > 1 && Index < 3) {
                Append(i, Index);
                continue;
            }
            i++;
            Append(i, Index);
            endLine = true;
        }
        return OutString.append("\n").toString();
    }

    public static String FightMenuOut(Room r){
        OutString = new StringBuilder();

        int Index = 0;

        int i = 6;

        for(Creature mob : r.RoomCreatures){
            OutString.append("Mob: ")
                    .append(mob.Name)
                    .append(" Level ")
                    .append(mob.Level)
                    .append(" HP: ")
                    .append(mob.HP)
                    .append(" Armor: ")
                    .append(mob.getArmor())
                    .append("\n");
        }

        while(i < 9){
            Index++;
            OutString.append(" [").append(Index).append("] ");
            Append(i, Index);
            i++;
        }
        OutString.append("\n");
        return OutString.toString();
    }

    static String TargetMenuOut(Room r){
        OutString = new StringBuilder();

        int Index = 0;
        Encounter.SetLength(r.RoomCreatures.size());
        for(Creature mob : r.RoomCreatures) {
            Index++;
            OutString.append(" [").append(Index).append("] ");
            OutString.append(mob.Name).append(" ");
            Encounter.InsertTarget(Index-1, ((Mob) mob));
        }
        OutString.append("\n");
        return OutString.toString();
    }

    static void Append(int i, int Index){
        OutString.append(ActionNames[i]);
        actions[Index] = GetAction(i+1);
    }

    static Actions GetAction(int Index){
        return switch (Index) {
            case 1 -> Actions.NEXTROOM;
            case 2 -> Actions.BACK;
            case 3 -> Actions.CHECHINV;
            case 4 -> Actions.GOSHOP;
            case 5 -> Actions.BUY;
            case 6 -> Actions.EXIT;
            case 7 -> Actions.ATK;
            case 8 -> Actions.DEF;
            case 9 -> Actions.ESC;
            default -> Actions.CHECHINV;
        };
    }


    public static void ShowPrompt(Player p, Room r, GameLoop.Options options){
        options.show(p,r, options);
    }

    public static void ShowInventory(Player p){
        System.out.printf("Items %d/%d \n", p.Inventory.size(), p.Inventory.size());
        for(Item item : p.Inventory){
            System.out.printf("%s, %d, %b \n", item.name, item.SellPrice, item.Wearable);
        }
    }

}
