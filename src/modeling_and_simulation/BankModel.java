/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modeling_and_simulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author Ahsan Khan
 */
public class BankModel {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        File file = new File("C:\\Users\\Ahsan Khan\\Documents\\NetBeansProjects\\modeling_and_simulation\\src\\modeling_and_simulation\\input.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st = br.readLine();

        String[] split = st.split(",");
        String temp = "";
        File fileout = new File("C:\\Users\\Ahsan Khan\\Documents\\NetBeansProjects\\modeling_and_simulation\\src\\modeling_and_simulation\\output.txt");
        BufferedWriter pw;
        pw = new BufferedWriter(new FileWriter(fileout));
        for (int i = 0; i < split.length; i++) {

            switch (i) {
                case 0:
                    temp += "Multi teller bank with Saprate Queues and Jockeying\n";

                    temp += "Number Of tellers:\t" + split[i] + "-" + split[i + 1] + "\n";

                    break;
                case 1:

                    break;
                case 2:
                    temp += "Mean arrival Time:\t" + split[i] + "minutes\n";
                    //pw.write(temp);
                    break;
                case 3:
                    temp += "Mean Service Time:\t" + split[i] + "minutes\n";

                    break;
                case 4:
                    temp += "Bank closes after:\t" + split[i] + "hours\n";

                    break;
                default:
                    throw new AssertionError();
            }
            pw.write(temp);
            temp = "";

        }
        br.close();
        pw.close();
        BankModel Bm = new BankModel();
        Bm.min_tellers = Integer.parseInt(split[0]);
        Bm.max_tellers = Integer.parseInt(split[1]);
        Bm.mean_inter_arrival = Integer.parseInt(split[2]);
        Bm.mean_service = Integer.parseInt(split[3]);
        Bm.length_door_open = Integer.parseInt(split[4]);
        for (Bm.num_tellers = Bm.min_tellers; Bm.num_tellers <= Bm.max_tellers; ++Bm.num_tellers) {
            //Initialize Simulation Library
            //init_simlib();
//event_schedule()
        }

    }
    int min_tellers;
    int max_tellers;
    int num_tellers;
    int shortest_length;
    int shortest_Queue;
    float mean_inter_arrival;
    float mean_service;
    float length_door_open;

    BankModel() {
        this.min_tellers = 1;
        this.max_tellers = 5;
        this.shortest_length = 2;
        this.shortest_Queue = 2;
        this.mean_service = 1;
        this.length_door_open = 3;
        this.mean_inter_arrival = 5;

    }

    void arrive() {//arrive start
        int teller;
        event_schedule(sim_time + expon(mean_interarrival, STREAM_INTERARRIVaL_TIME), EVENT_ARRIVAL);
        for (teller = 1; teller <= this.num_tellers; ++teller) {
            if (list_size[num_tellers + teller] == 0) {
                sampst(0, SAMPST_DELAYS);
                list_files(FIRST, num_tellers + teller);
                transfer[3] = teller;
                event_schedule(sim_time + expon(mean_service, STREAM_SERVICE), EVENT_DEPARTURE);

            }
        }
        shortest_length = list_size[1];
        this.shortest_Queue = 1;
        for (teller = 2; teller <= num_tellers; ++teller) {
            if (list_size[teller] < this.shortest_length) {
                this.shortest_length = list_size[teller];
                this.shortest_Queue = teller;
            }
            transfer[1] = sim_time;
            list_file(LAST, this.shortest_Queue);

        }
    }//arrive ends

    void depart(int teller) {
        if (list_size[teller] == 0) {
            list_remove(FIRST, num_tellers + teller);
        } else {
            list_remove(FIRST, teller);
            sampst(sim_time - transfer[1], SAMPST_DELAYS);
            transfer[3] = teller;
            event_schedule(sim_time + expon(mean_service, STREAM_SERVICE), EVENT_DEPARTURE);
        }
        this.jockey(teller);
    }//depart ends

    void jockey(int teller) {
        int jumper, min_distance, ni, nj, other_teller, distance;
        jumper = 0;
        min_distance = 1000;
        ni = list_size[teller] + list_size[this.num_tellers + teller];
        for (other_teller = 1; other_teller <= this.num_tellers; ++other_teller) {
            nj = list_size[other_teller] + list_size[this.num_tellers + other_teller];
            distance = Math.abs(teller - other_teller);
            if ((other_teller != teller) && (nj > ni + 1) && (distance < min_distance)) {
                jumper = other_teller;
                min_distance = distance;
            }
        }

        if (jumper > 0) {
            list_remove(LAST, jumper);
            if (list_size[this.num_tellers + teller] > 0) {
                list_file(LAST, teller);

            } else {
                sampst(sim_time - transfer[1], SAMPST_DELAYS);
                list_file(FIRST, this.num_tellers + teller);
                transfer[3] = teller;
                event_schedule(sim_time + expon(this.mean_service, STREAM_SERVICE), EVENT_DEPARTURE);

            }
        }

    }//jockey ends

    void report() {
        int teller;
        float avg_num_in_queue = 0;
        for (teller = 1; teller <= this.num_tellers; ++teller) {
            avg_num_in_queue += filest(teller);
            String temp = "";
            temp = "\n\nwith" + num_tellers + ",average number in queue=" + avg_num_in_queue;
            pw.write(temp);
            temp = "\n\ndelays in queue in minutes:\n";
            pw.write(temp);
            pw.write(out_sampst(SAMPST_DELAYS, SAMPST_DELAYS));
            temp = "";

        }
    }

}
