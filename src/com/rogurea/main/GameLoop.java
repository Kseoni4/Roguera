package com.rogurea.main;

public class GameLoop {



    static enum Options {
        ActionMenu{
            public void show(Player p, Room r, Options option){
                View.Print(p, r, option);
            }
        },
        FightMenu{
            public void show(Player p, Room r, Options option){
                View.Print(p, r, option);
            }
        },
        TargetMenu{
            public void show(Player p, Room r, Options option){
                View.Print(p, r, option);
            }
        };
        public abstract void show(Player p, Room r, Options option);
    }

    static Options option;

    static boolean q = false;

    static int FirstRoom = 1;

    static boolean IsDef = false;

    public static void Start(Player player){
        while(!q){
            if(player.CurrentRoom == FirstRoom){
                StartRoom(player, Dungeon.Rooms.get(FirstRoom-1));
            }
            else
                CurrentRoom(player, Dungeon.Rooms.get(player.CurrentRoom-1));
        }
    }

    static void CurrentRoom(Player p, Room room){
        p.CurrentRoom = room.NumberOfRoom;
        if(room.RoomCreatures.isEmpty())
            try{
                ActionDo(GetChooseAction(p, room), p);
            }
            catch (NullPointerException ignored) {

            }
        else
            GoFight(p, room);

    }

    static void GoFight(Player p, Room room){

        int turns = 1;

        while(!room.RoomCreatures.isEmpty()){
            IsDef = false;
            System.out.printf("Turn: %d \n", turns);
            ActionDo(GetChooseActionFight(p, room), p);

            if(!IsDef){
                Encounter.SetTarget();
                Encounter.Attack(Encounter.target, p);
                Encounter.RemoveCreature(Encounter.target, room);
                Input.WaitFromPlayer();
            }
            Encounter.MobAttack(room.RoomCreatures, p);
        };
    }


    static void StartRoom(Player p, Room room){
        System.out.print("Welcome to the start room! \n");
        ActionDo(GetChooseAction(p, room), p);
    }

    static View.Actions GetChooseAction(Player p, Room room){
        View.ShowPrompt(p, room, Options.ActionMenu);
        return View.actions[Input.WaitFromPlayer()];
    }

    private static View.Actions GetChooseActionFight(Player p, Room r) {
        View.ShowPrompt(p, r, Options.FightMenu);
        return View.actions[Input.WaitFromPlayer()];
    }

    private static void GoBack(Player player){
        player.CurrentRoom--;
        CurrentRoom(player, Dungeon.Rooms.get((player.CurrentRoom-1)));
    }

    static void ActionDo (View.Actions actions, Player player) {
        switch (actions){
            case NEXTROOM -> CurrentRoom(player, Dungeon.Rooms.get((player.CurrentRoom-1)).nextRoom);
            case BACK, ESC -> GoBack(player);
            case CHECHINV -> View.ShowInventory(player);
            case ATK -> View.ShowPrompt(player, Dungeon.Rooms.get(player.CurrentRoom-1), Options.TargetMenu);
            case DEF -> IsDef = true;
        }
    }
}
