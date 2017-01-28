package com.bortolan.iquadriv2.Adapters;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bortolan.iquadriv2.Interfaces.MarkSubject;
import com.bortolan.iquadriv2.Interfaces.Media;
import com.bortolan.iquadriv2.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ArcProgressStackView;

import static com.bortolan.iquadriv2.Utils.Methods.MessaggioVoto;
import static com.bortolan.iquadriv2.Utils.Methods.getMediaColor;
import static com.bortolan.iquadriv2.Utils.Methods.isAppInstalled;

public class AdapterMedie extends RecyclerView.Adapter<AdapterMedie.MedieHolder> {
    final private String TAG = AdapterMedie.class.getSimpleName();

    private final List<MarkSubject> CVDataList;
    private final Context mContext;
    private int period;

    public AdapterMedie(Context context, int periodo) {
        this.mContext = context;
        this.period = periodo;
        this.CVDataList = new LinkedList<>();
    }


    public void addAll(Collection<MarkSubject> list) {
        CVDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public MedieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_medie_grid, parent, false);
        return new MedieHolder(v);
    }


    @Override
    public void onBindViewHolder(MedieHolder ViewHolder, int position) {
        final MarkSubject marksubject = CVDataList.get(position);

        Media media = new Media();
        media.setMateria(marksubject.getName());
        media.addMarks(marksubject.getMarks());
        ViewHolder.mTextViewMateria.setText(media.getMateria());

        ViewHolder.mCardViewMedia.setOnClickListener(v -> {
            if (isAppInstalled(mContext, "com.sharpdroid.registroelettronico")) {
                // TODO: 28/01/2017 update
                Intent LaunchIntent = mContext.getPackageManager().getLaunchIntentForPackage("com.sharpdroid.registroelettronico");
                //Intent LaunchIntent = new Intent("com.sharpdroid.registroelettronico.DETAILS");
                //LaunchIntent.putExtra("data", new Gson().toJson(marksubject));
                mContext.startActivity(LaunchIntent);
            } else {
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.sharpdroid.registroelettronico")));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "Nessuna applicazione può aprire questo link", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (media.containsValidMarks()) {
            ViewHolder.mTextViewMedia.setText(String.format(Locale.getDefault(), "%.2f", media.getMediaGenerale()));
            final float voto_obiettivo = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getString("voto_obiettivo", "8"));
            List<ArcProgressStackView.Model> models = new ArrayList<>();
            models.add(new ArcProgressStackView.Model("media", media.getMediaGenerale() * 10, ContextCompat.getColor(mContext, getMediaColor(media, voto_obiettivo))));
            ViewHolder.mArcProgressStackView.setModels(models);
            String obbiettivo_string = MessaggioVoto(voto_obiettivo, media.getMediaGenerale(), media.getNumeroVoti());
            ViewHolder.mTextViewDesc.setText(obbiettivo_string);
        } else {
            List<ArcProgressStackView.Model> models = new ArrayList<>();
            models.add(new ArcProgressStackView.Model("media", 100, ContextCompat.getColor(mContext, R.color.intro_blue)));
            ViewHolder.mArcProgressStackView.setModels(models);
            ViewHolder.mTextViewMedia.setText("-");
            ViewHolder.mTextViewDesc.setText(mContext.getString(R.string.nessun_voto_numerico));
        }
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    class MedieHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardview_medie)
        CardView mCardViewMedia;
        @BindView(R.id.progressvoti)
        ArcProgressStackView mArcProgressStackView;
        @BindView(R.id.materia)
        TextView mTextViewMateria;
        @BindView(R.id.media)
        TextView mTextViewMedia;
        @BindView(R.id.descrizione)
        TextView mTextViewDesc;

        MedieHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
