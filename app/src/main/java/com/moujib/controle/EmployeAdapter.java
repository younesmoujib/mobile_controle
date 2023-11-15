package com.moujib.controle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmployeAdapter extends RecyclerView.Adapter<EmployeAdapter.viewHolder> {

 private Context context;
 private List<Employe> employes;

    public EmployeAdapter(Context context, List<Employe> employes) {
        this.context = context;
        this.employes = employes;
    }

    @NonNull
    @Override
    public EmployeAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeAdapter.viewHolder holder, int position) {

        Employe employe = employes.get(position);
        // Convertissez le tableau de bytes en Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(employe.getPhoto(), 0, employe.getPhoto().length);
        // Affichez le Bitmap dans l'ImageView
        holder.imageView.setImageBitmap(bitmap);
        holder.id.setText(String.valueOf(employe.getId()));
        holder.nom.setText(employe.getNom());
        holder.prenom.setText(employe.getPrenom());


    }

    @Override
    public int getItemCount() {
        return employes.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView id,nom,prenom;
        ConstraintLayout parent;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img2);
            id=itemView.findViewById(R.id.id1);
            nom=itemView.findViewById(R.id.Nom);
            prenom = itemView.findViewById(R.id.prenom);
            parent=itemView.findViewById(R.id.prenom1);

        }
    }
}
