package demo;

import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;

import common.Game;
import common.Machine;

public class Game_demo extends Game {

	@Override
	public void addMachines(ArrayList<Machine> machines, int numFaulty) {
        for(int i = 0; i<machines.size(); i++){
            all_machines.add(machines.get(i));
        }

        this.numFaulty = numFaulty;

        for(int i = 0; i<all_machines.size(); i++){
            all_machines.get(i).setMachines(all_machines);
        }
	}

	@Override
	public void startPhase() {
        //tagging t machines as faulty
        int count = 0;
        Random rand = new Random();
        while(count != numFaulty){
            int rand_int = rand.nextInt(all_machines.size());
            if(!faulty_machines.contains(rand_int)){
                faulty_machines.add(rand_int);
                count++;
            }
        }

        for(int i = 0; i<all_machines.size(); i++){
            if(faulty_machines.contains(i)){
                all_machines.get(i).setState(false);
            }
            else{
                all_machines.get(i).setState(true);
            }
        }

        //sending leaderinfo to all machines (if not faulty) and to 2t + 1 machines (if faulty)
        ArrayList<Integer> tagged_machines = new ArrayList<Integer>();
        Leader_id = rand.nextInt(all_machines.size());
        Leader_decision = rand.nextInt(2);
        int tempfault = rand.nextInt(2);
        if(tempfault == 0){
            isLeaderFaulty = false;
        }
        else{
            isLeaderFaulty = true;
        }

        all_machines.get(Leader_id).setState(isLeaderFaulty);
        all_machines.get(Leader_id).setLeader();

        if(!isLeaderFaulty){
            for(Machine m : all_machines){
                m.sendMessage(Leader_id, phase, 0, Leader_decision);
            }
        }
        else{
            //leader can send a message to itself
            int tag = rand.nextInt(all_machines.size() - 2*numFaulty) + 2*numFaulty + 1;
            int tagcount = 0;
            while(tagcount != tag){
                int rand_tag = rand.nextInt(all_machines.size());
                if(!tagged_machines.contains(rand_tag)){
                    tagged_machines.add(rand_tag);
                //we have to delete all messages for faulty machines too
                    tagcount++;
                }
            }

            for(int i = 0; i<tagged_machines.size(); i++){
                all_machines.get(tagged_machines.get(i)).sendMessage(Leader_id, phase, 0, Leader_decision);
            }
        }

        phase++;
        faulty_machines.clear();
        tagged_machines.clear();
	}

    public void startPhase(int leaderId, ArrayList<Boolean> areCorrect) {
        //tagging t machines as faulty

        Random rand = new Random();
        for(int i = 0; i<areCorrect.size(); i++){
            if(areCorrect.get(i)){
                all_machines.get(i).setState(true);
            }
            else{
                all_machines.get(i).setState(false);
            }
        }

        //sending leaderinfo to all machines (if not faulty) and to 2t + 1 machines (if faulty)
        ArrayList<Integer> tagged_machines = new ArrayList<Integer>();
        Leader_decision = rand.nextInt(2);

        boolean isLeaderCorrect = areCorrect.get(leaderId);

        all_machines.get(leaderId).setState(isLeaderCorrect);
        all_machines.get(leaderId).setLeader();

        if(isLeaderCorrect){
            for(Machine m : all_machines){
                m.sendMessage(leaderId, phase, 0, Leader_decision);
            }
        }
        else{
            //leader can send a message to itself
            int tag = rand.nextInt(all_machines.size() - 2*numFaulty) + 2*numFaulty + 1;
            int tagcount = 0;
            while(tagcount != tag){
                int rand_tag = rand.nextInt(all_machines.size());
                if(!tagged_machines.contains(rand_tag)){
                    tagged_machines.add(rand_tag);
                //we have to delete all messages for faulty machines too
                    tagcount++;
                }
            }

            for(int i = 0; i<tagged_machines.size(); i++){
                all_machines.get(tagged_machines.get(i)).sendMessage(leaderId, phase, 0, Leader_decision);
            }
        }

        phase++;
        tagged_machines.clear();
	}
    private int round = 0;
    private int phase = 0;
    private int Leader_id;
    private boolean isLeaderFaulty;
    private int Leader_decision;
    private int numFaulty;
    private ArrayList<Machine> all_machines = new ArrayList<Machine>();
    private ArrayList<Integer> faulty_machines = new ArrayList<Integer>();
}
