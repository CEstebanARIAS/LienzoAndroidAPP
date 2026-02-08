package jdc.esteban.lienzopaintandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private PaintView paintView;
    private Spinner spinnerTools;
    private LinearLayout toolSubBar;
    private ImageButton selectedButton, imgBtnLapiz, imgBtnBorrar, imgBtnMover, imgBtnTexto, imgBtnCirculo, imgBtnLineaFlecha, imgBtnLinea, imgBtnFlecha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        paintView =findViewById(R.id.paintView);
        spinnerTools = findViewById(R.id.spinnerTools);
        imgBtnLapiz = findViewById(R.id.imgBtnLapiz);
        imgBtnBorrar = findViewById(R.id.imgBtnBorrar);
        imgBtnMover = findViewById(R.id.imgBtnMover);
        imgBtnTexto = findViewById(R.id.imgBtnTexto);
        imgBtnCirculo = findViewById(R.id.imgBtnCirculo);
        imgBtnLineaFlecha = findViewById(R.id.imgBtnLineaFlecha);
        imgBtnLinea = findViewById(R.id.imgBtnLinea);
        imgBtnFlecha = findViewById(R.id.imgBtnFlecha);

        toolSubBar = findViewById(R.id.toolSubBar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tools_array,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerTools.setAdapter(adapter);

        spinnerTools.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position){
                    case 0:
                        paintView.setTool(PaintView.Tool.PEN);
                        break;
                    case 1:
                        paintView.setTool(PaintView.Tool.ERASER);
                        break;
                    case 2:
                        paintView.setTool(PaintView.Tool.CIRCLE);
                        break;
                    case 3:
                        paintView.setTool(PaintView.Tool.RECTANGLE);
                        break;
                    case 4:
                        paintView.setTool(PaintView.Tool.TEXT);
                        break;
                    case 5:
                        paintView.setTool(PaintView.Tool.MOVE);
                        break;
                }
                PaintView.Tool tool = PaintView.Tool.values()[position];
                paintView.setTool(tool);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imgBtnLapiz.setOnClickListener(v ->{
            selectTool(imgBtnLapiz, PaintView.Tool.PEN);
        });
        imgBtnCirculo.setOnClickListener(v ->{
            selectTool(imgBtnCirculo, PaintView.Tool.CIRCLE);
        });
        imgBtnMover.setOnClickListener(v ->{
            selectTool(imgBtnMover, PaintView.Tool.MOVE);
        });
        imgBtnTexto.setOnClickListener(v ->{
            selectTool(imgBtnTexto, PaintView.Tool.TEXT);
        });
        imgBtnBorrar.setOnClickListener(v ->{
            selectTool(imgBtnBorrar, PaintView.Tool.ERASER);
        });
        imgBtnLineaFlecha.setOnClickListener(v ->{
            selectTool(imgBtnLineaFlecha, PaintView.Tool.LINE);
        });
        imgBtnLinea.setOnClickListener(v ->{
            selectLineType(imgBtnLinea, PaintView.TipoLinea.LINEARECTA);
        });
        imgBtnFlecha.setOnClickListener(v ->{
            selectLineType(imgBtnFlecha, PaintView.TipoLinea.FLECHA);
        });

        paintView.setOnTextEditListener(textShape -> {

            EditText input = new EditText(this);
            input.setText(textShape.getText());

            new AlertDialog.Builder(this)
                    .setTitle("Editar texto")
                    .setView(input)
                    .setPositiveButton("OK", (d, w) -> {
                        textShape.setText(input.getText().toString());
                        paintView.invalidate();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void selectTool(ImageButton button, PaintView.Tool tool) {

        selectButton(button,
                imgBtnLapiz, imgBtnCirculo, imgBtnTexto,
                imgBtnMover, imgBtnBorrar,
                imgBtnLineaFlecha
        );

        // lógica de UI
        toolSubBar.setVisibility(
                tool == PaintView.Tool.LINE ? View.VISIBLE : View.GONE
        );

        // lógica de negocio
        paintView.setTool(tool);
    }

    private void selectButton(ImageButton selected, ImageButton... group) {
        for (ImageButton btn : group) {
            btn.setSelected(false);
        }
        selected.setSelected(true);
    }

    private void selectLineType(
            ImageButton button,
            PaintView.TipoLinea tipo
    ) {
        selectButton(button, imgBtnLinea, imgBtnFlecha);
        paintView.setTipoLinea(tipo);
    }



}