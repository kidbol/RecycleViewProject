package com.example.recycleviewproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.view.ActionMode;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.recycleviewproject.model.Email;
import com.example.recycleviewproject.model.Emails;
import com.mooveit.library.Fakeit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EmailAdapter emailAdapter;

    private ActionMode actionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fakeit.init();
        setContentView(R.layout.activity_main);

        emailAdapter = new EmailAdapter(new ArrayList<>(Emails.fakeEmails()));

        final RecyclerView rv = findViewById(R.id.recycleviewmain);
        rv.setAdapter(emailAdapter);

        findViewById(R.id.btn_float).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEmail();
                rv.scrollToPosition(0);
            }
        });

        ItemTouchHelper helper =
                new ItemTouchHelper(new ItemTouchHandler(0,
                        ItemTouchHelper.LEFT));

        helper.attachToRecyclerView(rv);

        emailAdapter.setListner(new EmailAdapter.EmailAdapterListner() {
            @Override
            public void onItemClick(int position) {
                enableActionMode(position);
            }

            @Override
            public void onItemLongClick(int position) {
                enableActionMode(position);
            }
        });
    }

    private void enableActionMode(int position){

        if(actionMode == null){

            actionMode = startSupportActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                    mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    if(item.getItemId() == R.id.action_delete){
                        emailAdapter.deleteEmails();
                        mode.finish();

                    }

                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });

        }


        emailAdapter.toggleSelection(position);

        final int size = emailAdapter.selectedItens.size();
        if(size == 0){
            actionMode.finish();
        }
        else{
            actionMode.setTitle(size + "");
            actionMode.invalidate();
        }

    }

    private class ItemTouchHandler extends ItemTouchHelper.SimpleCallback{


        public ItemTouchHandler(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();

            Collections.swap(emailAdapter.getEmails(), from, to);
            emailAdapter.notifyItemMoved(from, to);

            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            emailAdapter.getEmails().remove(viewHolder.getAdapterPosition());
            emailAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

        }
    }


    private void addEmail(){
        try{

            Date sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")).parse(Fakeit.dateTime().dateFormatter());

            StringBuilder preview = new StringBuilder();
            for(int i = 0; i < 10; i++){
                preview.append(Fakeit.lorem().words());
                preview.append(" ");
            }

            emailAdapter.getEmails().add(0, Email.EmailBuilder.builder()
                    .setStarted(false)
                    .setUnread(true)
                    .setUser(Fakeit.name().firstName())
                    .setSubject(Fakeit.company().name())
                    .setDate(new SimpleDateFormat("d MMM", new Locale("pt", "BR")).format(sdf))
                    .setPreview(preview.toString())
                    .build()

            );

        }
        catch (ParseException e){

        }

        emailAdapter.notifyItemInserted(0);


    }



}