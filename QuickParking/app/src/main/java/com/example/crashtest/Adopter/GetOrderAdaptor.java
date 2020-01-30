package com.example.crashtest.Adopter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.crashtest.R;
import com.example.crashtest.VehicalInformation;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.List;
import java.util.Random;

public class GetOrderAdaptor extends RecyclerView.Adapter<GetOrderAdaptor.MyViewHolder>{
    private Context context;
    private List<VehicalInformation> vehicalInformations;


    public GetOrderAdaptor(Context context, List<VehicalInformation> vehicalInformations) {
        this.context = context;
        this.vehicalInformations = vehicalInformations;
    }

    @NonNull
    @Override
    public GetOrderAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.order_view,parent,false);
        return new GetOrderAdaptor.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GetOrderAdaptor.MyViewHolder holder, final int position) {

        VehicalInformation v=vehicalInformations.get(position);
        holder.orderName.setText(v.getVehicalCompany());
        holder.orderNumber.setText(v.getVehicalNumber());
        holder.orderOwner.setText(v.getOwnerName());
        holder.orderBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                View orderInput=LayoutInflater.from(context).inflate(R.layout.orer_back_input,null);

                final TextView orderAddress=orderInput.findViewById(R.id.orderBackAddress);
                final Button orderBackBtn=orderInput.findViewById(R.id.orderBackBtn);

                final AlertDialog inputs= new AlertDialog.Builder(context).setView(orderInput).setTitle("Order Inputs").setNegativeButton("Cancel",null).create();
                inputs.show();



                holder.orderQR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View qrView=LayoutInflater.from(context).inflate(R.layout.qr_code,null);
                        final int num =new Random().nextInt(40-30+1)+30;
                        ImageView qr=qrView.findViewById(R.id.ImageQR);
                        new AlertDialog.Builder(context).setView(qrView).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new androidx.appcompat.app.AlertDialog.Builder(context).setMessage("Your Order Is Submited Successfully your fee is|| R.s "+num+"  ||")
                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ((Activity)context).finish();

                                            }
                                        })
                                        .setTitle("Message")
                                        .setCancelable(false)
                                        .create()
                                        .show();
                                dialog.dismiss();

                            }
                        }).create().show();


                        holder.generateQRCode("Ali"+num,qr);
                    }
                });

                orderBackBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(orderAddress.getText().toString().isEmpty()){

                            orderAddress.setError("Address Must not empty");
                            orderAddress.requestFocus();
                        }else{

                          AlertDialog box=  new AlertDialog.Builder(context).setMessage("Your Order now in way to your address please wait for 35mints thank you").setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    inputs.dismiss();
                                    dialog.dismiss();
                                    holder.orderQR.setVisibility(View.VISIBLE);
                                    holder.orderBackBtn.setVisibility(View.GONE);
                                }
                            }).create();
                          box.show();
                        }
                    }
                });









            }
        });

    }

    @Override
    public int getItemCount() {
        return vehicalInformations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{



        public TextView orderName;
        public TextView orderNumber;
        public TextView orderOwner;
        public Button orderBackBtn;
        public Button orderQR;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            orderName=itemView.findViewById(R.id.orderTitle);
            orderNumber=itemView.findViewById(R.id.orderNumber);
            orderOwner=itemView.findViewById(R.id.orderOwner);
            orderBackBtn=itemView.findViewById(R.id.orderBackBtn);
            orderQR=itemView.findViewById(R.id.orderQRGenerator);

        }

        public void generateQRCode(String qrContent, ImageView qrSetLocation){

            QRCodeWriter qrCodeWriter=new QRCodeWriter();
            BitMatrix bitMatrix= null;
            try {
                bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 400, 400);
                int height=bitMatrix.getHeight();
                int width=bitMatrix.getWidth();
                Bitmap bmp=Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
                for(int x=0;x<width;x++){
                    for(int y=0;y<height;y++){
                        bmp.setPixel(x,y,bitMatrix.get(x,y)? Color.RED:Color.YELLOW);
                    }
                }
                qrSetLocation.setImageBitmap(bmp);
            } catch (WriterException e) {
                e.printStackTrace();
            }



        }
    }

}
