package ru.engineers.my_team.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.engineers.my_team.databinding.TeamListActivityBinding;

import java.util.ArrayList;

import ru.engineers.my_team.system.EmployeeModel;
import ru.engineers.my_team.system.SharedPreferencesHelper;
import ru.engineers.my_team.system.TeamListRecyclerViewAdapter;

public class TeamListActivity extends AppCompatActivity {

    // Помощник для работы с Shared Prefs
    public SharedPreferencesHelper<EmployeeModel> sharedPreferencesHelper;
    // Биндинг Вьюх
    public TeamListActivityBinding viewBinding;
    // Локальный список сотрудников
    public ArrayList<EmployeeModel> employeeModels;
    // Адаптер для списка сотрудников
    public TeamListRecyclerViewAdapter adapter;

    @Override
    public void onPause() {
        super.onPause();
        // При сворачивании Активити сохранить все изменения списка сотрудников
        sharedPreferencesHelper.saveModelsArrayList("employees", employeeModels);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Биндинг Вьюх и развертка верстки
        viewBinding = TeamListActivityBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        // Создаем помощника работы с Shared Prefs
        sharedPreferencesHelper = new SharedPreferencesHelper<>(this, EmployeeModel.class);

        // Получение списка сотрдуников из Shared Prefs
        employeeModels = sharedPreferencesHelper.getModelsArrayList("employees");

        // Настройка адаптера списка сотрудников
        viewBinding.recyclerView.setHasFixedSize(true);
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Внутри Адаптера понадобится ссылка на текущий Активити
        adapter = new TeamListRecyclerViewAdapter(employeeModels, this);
        viewBinding.recyclerView.setAdapter(adapter);
        // Настройка свайпов
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT));
        itemTouchHelper.attachToRecyclerView(viewBinding.recyclerView);

        // Добавление коллбека нажатия на кнопку
        viewBinding.buttonAddMember.setOnClickListener(new AddButtonOnCLickListener());
    }

    /**
     * Класс, который содержит коллбек нажатия на кнопку добавление сотрудника
     */
    public class AddButtonOnCLickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(TeamListActivity.this, AddingEmployeeActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Класс с функционалом свайпов
     */
    public class SimpleTouchHelper extends ItemTouchHelper.SimpleCallback {

        public SimpleTouchHelper(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        /**
         * Если свайпнуто, то показываем Тост, удаляем из списка сотрудников человека, говорим адаптеру, что данные изменились
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Toast.makeText(TeamListActivity.this, "Уволен!", Toast.LENGTH_SHORT).show();
            int position = viewHolder.getAdapterPosition();
            employeeModels.remove(position);
            adapter.notifyDataSetChanged();
        }
    }
}
