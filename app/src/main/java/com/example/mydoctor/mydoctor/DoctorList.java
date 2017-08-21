package com.example.mydoctor.mydoctor;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

/**
 * Created by Wenso on 8/8/2016.
 */
public class DoctorList extends Fragment {


    RecyclerView recyclerView;
    ArrayList<String> mobileArray, mobileArray1, mobileArray2;
    String[] values, values1;
    String ItemSelected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         ItemSelected=Check_online.itemSelectedd;

        mobileArray=new ArrayList<String>();
        mobileArray1=new ArrayList<String>();
        mobileArray2=new ArrayList<String>();

        mobileArray.add("Dr.James");
        mobileArray.add("Dr.Adithya");
        mobileArray.add("Dr.Hussain");
        mobileArray.add("Dr.Allam");
        mobileArray.add("Dr.Sandeep");
        mobileArray.add("Dr.Peter");
        mobileArray.add("Dr.Plater");
        mobileArray.add("Dr.Senthil");


        mobileArray1.add("3 years(Cardiologist)");
        mobileArray1.add("6 years(Cardiologist)");
        mobileArray1.add("10 years(Cardiologist)");
        mobileArray1.add("11 years(Cardiologist)");
        mobileArray1.add("20 years(Cardiologist)");
        mobileArray1.add("8 years(Cardiologist)");
        mobileArray1.add("18 years(Cardiologist)");
        mobileArray1.add("15 years(Cardiologist)");


        mobileArray2.add("Manchester");
        mobileArray2.add("London");
        mobileArray2.add("Birmingham");
        mobileArray2.add("Leeds");
        mobileArray2.add("Liverpool");
        mobileArray2.add("London");
        mobileArray2.add("Bombay");
        mobileArray2.add("Mumbai");

    }

    public DoctorList() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.doctor_list,container,false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView=(RecyclerView)view.findViewById(R.id.recycle);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(layoutManager);
        adapter adptr=new adapter();
       recyclerView.setAdapter(adptr);


    }

    public class adapter extends RecyclerView.Adapter<adapter.viewHolder>{

        public class viewHolder extends RecyclerView.ViewHolder{
            TextView TV1, TV2, TV3;
            ImageView IVVprofile_image;
            LinearLayout LLtext;

            public viewHolder(View itemView) {
                super(itemView);

                 TV1=(TextView)itemView.findViewById(R.id.tv1);
                 TV2=(TextView)itemView.findViewById(R.id.tv2);
                 TV3=(TextView)itemView.findViewById(R.id.tv3);
                 IVVprofile_image=(ImageView)itemView.findViewById(R.id.doc_profile_image);
                 LLtext=(LinearLayout)itemView.findViewById(R.id.LLviewUser);

            }
        }

        @Override
        public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(getContext()).inflate(R.layout.doctor_list_single,parent,false);
            return new viewHolder(view);
        }

        @Override
        public void onBindViewHolder(final viewHolder holder, int position) {

            holder.TV1.setText(mobileArray.get(position));
            holder.TV2.setText(ItemSelected);
            holder.TV3.setText(mobileArray2.get(position));

            holder.IVVprofile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  getFragmentManager().beginTransaction().replace(R.id.main, new DoctorMainFragment()).addToBackStack(null).commit();
                    Intent in=new Intent(getActivity(),DoctorProfile.class);

                    View sharedView = holder.IVVprofile_image;
                    String transitionName = getString(R.string.TransitionName);

                    ActivityOptions transitionActivityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedView, transitionName);
                    }
                    startActivity(in, transitionActivityOptions.toBundle());



                }
            });

            holder.LLtext.setOnClickListener(new View.OnClickListener() {

                ProgressDialog progress;
                @Override
                public void onClick(View view) {

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //to remove the title on ALertDialog

                    dialog.setContentView(R.layout.dialog_box);
                   Button Schedule = (Button) dialog.findViewById(R.id.Schedule);
                   Button RequestAppointment = (Button) dialog.findViewById(R.id.RequestAppointment);
                    TextView availableTimings = (TextView) dialog.findViewById(R.id.Timings);

                    Schedule.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog dialog1 = new Dialog(getActivity());
                            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE); //to remove the title on ALertDialog

                            dialog1.setContentView(R.layout.date_time_picker);
                            Button pick_date=(Button)dialog1.findViewById(R.id.pick_date);
                            Button pick_time=(Button)dialog1.findViewById(R.id.pick_time);
                            final DatePicker date_picker=(DatePicker) dialog1.findViewById(R.id.date_picker);
                            final TimePicker time_picker=(TimePicker) dialog1.findViewById(R.id.time_picker);

                            Button button=(Button)dialog1.findViewById(R.id.btnOKK);

                         dialog1.show();
                           dialog.dismiss();

                       pick_date.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                              // showDatePickerDialog();
                              date_picker.setVisibility(View.VISIBLE);
                               time_picker.setVisibility(View.GONE);
                           }

                       /*    private void showDatePickerDialog() {
                               DialogFragment newFragment = new DatePickerFragment();
                               newFragment.show(getFragmentManager(), "datePicker");
                           }*/
                       });

                            pick_time.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    date_picker.setVisibility(View.GONE);
                                    time_picker.setVisibility(View.VISIBLE);
                                }

                           /*     private void showTimePickerDialog() {
                                    DialogFragment newFragment = new TimePickerFragment();
                                    newFragment.show(getFragmentManager(), "timePicker");
                                }*/
                            });
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    dialog1.dismiss();

                                }
                            });
                           // dialog.dismiss();
                        }
                    });

                    RequestAppointment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // progress dialog

                             progress = new ProgressDialog(getActivity());
                            progress.setTitle("Waiting for Doctor's approval...");
                            //progress.setMessage("Waiting for Doctor's approval...");
                            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progress.setProgress(0);

                            progress.setIndeterminate(true);
                            progress.show();
                            dialog.dismiss();

                            Runnable runnable=new Runnable() {
                             @Override
                             public void run() {
                              progress.cancel();
                             }};

                            Handler PDCanceller = new Handler();
                            PDCanceller.postDelayed(runnable, 3000);

                        }

                    });
                    dialog.show();
                }
            });

        }
        @Override
        public int getItemCount() {
            return mobileArray.size();
        }
    }


}
