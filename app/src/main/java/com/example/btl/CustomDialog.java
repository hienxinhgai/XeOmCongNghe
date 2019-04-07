package com.example.btl;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CustomDialog extends Dialog{
    private Context context;
    private View.OnClickListener onClickListener;
    private DatXe d;
    private TextView tvDienDen,tvKhoangCach,tvGiaCa,tvChiPhi;
    private Button btnHuy,btnDatXe;
    public static EditText edtGhiChu;

    public CustomDialog(Context context, DatXe d,View.OnClickListener onClickListener) {
        super(context);
        this.context = context;
        this.d = d;
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        tvDienDen= (TextView) findViewById(R.id.tvDiemDen);
        tvKhoangCach = (TextView) findViewById(R.id.tvKhoangCach);
        tvGiaCa = (TextView) findViewById(R.id.tvGiaCa);
        tvChiPhi = (TextView) findViewById(R.id.tvChiPhi);
        edtGhiChu = (EditText) findViewById(R.id.edtGhiChu);
        btnHuy = (Button) findViewById(R.id.btnHuyDialog);
        btnDatXe = (Button) findViewById(R.id.btnDatXeDialog);
        tvDienDen.setText("Điểm đến: " + d.viTriDich);
        tvKhoangCach.setText("Khoảng cách: " + d.khoangCach + " km");
        tvGiaCa.setText("Giá cả: 10 000đ/1km đầu");
        tvChiPhi.setText("Chi phí: " + d.chiPhi + " đồng");


        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnDatXe.setOnClickListener(onClickListener);
    }

}
