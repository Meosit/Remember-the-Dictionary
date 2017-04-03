package by.mksn.rememberthedictionary.fragment.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import by.mksn.rememberthedictionary.R;
import by.mksn.rememberthedictionary.model.Phrase;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {

    private List<Phrase> phrases;

    public ListItemAdapter(List<Phrase> phrases) {
        this.phrases = phrases;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.translationText.setText(phrases.get(position).getTranslation());
        holder.phraseText.setText(phrases.get(position).getPhrase());
    }

    @Override
    public int getItemCount() {
        return phrases.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView phraseText;
        public TextView translationText;

        public ViewHolder(View itemView) {
            super(itemView);
            phraseText = (TextView) itemView.findViewById(R.id.list_view_item_phrase);
            translationText = (TextView) itemView.findViewById(R.id.list_view_item_translation);
        }
    }
}
