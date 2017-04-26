package pro.rane.foodadvisor;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;

import java.util.List;

import es.dmoral.toasty.Toasty;

class RVAdapter extends RecyclerView.Adapter<RVAdapter.ProductViewHolder>{

    private final static String imgURL = "http://foodadvisor.rane.pro:8080/getArticleImage?article_id=";
    private List<Product> products;
    private ImageLoader imageLoader;
    private ImageSize targetSize;
    Context context;
    private RequestQueue queue;

    RVAdapter(List<Product> products,Context context,RequestQueue queue){
        this.products = products;
        this.imageLoader = ImageLoader.getInstance();
        this.targetSize = new ImageSize(256, 256);
        this.context = context;
        this.queue = queue;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new ProductViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        holder.productName.setText(String.format("Nome: %s", products.get(position).name));
        holder.productDesc.setText(String.format("Descrizione: %s", products.get(position).description));
        holder.productId.setText(String.format("Id: %s", products.get(position).prodId));
        imageLoader.loadImage(imgURL.concat(products.get(position).prodId), targetSize, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.productImg.setImageResource(R.drawable.farmer);
                holder.productImg.setVisibility(View.INVISIBLE);
                holder.pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.productImg.setImageResource(R.drawable.image_not_found);
                holder.productImg.setVisibility(View.VISIBLE);
                holder.pb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.productImg.setImageBitmap(loadedImage);
                holder.productImg.setVisibility(View.VISIBLE);
                holder.pb.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                holder.productImg.setImageResource(R.drawable.image_not_found);
                holder.productImg.setVisibility(View.VISIBLE);
                holder.pb.setVisibility(View.INVISIBLE);
            }
        });
        holder.productBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String lifeURL = "http://foodadvisor.rane.pro:8080/getArticleLife?article_id=".concat(products.get(holder.getAdapterPosition()).prodId).concat("&seller_id=0");

                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, lifeURL,null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // TODO: 25/04/2017 riabilitare appena capisco cosa la map activity vuole che gli passi
                        Toasty.success(context,response.toString(), Toast.LENGTH_LONG).show();
                        Intent mapActivity = new Intent(context, MapsActivity.class);
                        mapActivity.putExtra("info", response.toString());
                        context.startActivity(mapActivity);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(context,"La richiesta non è andata a buon fine\nRiprova più tardi",Toast.LENGTH_SHORT).show();
                    }
                });


                queue.add(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView productName;
        TextView productDesc;
        TextView productId;
        ImageView productImg;
        Button productBtn;
        ProgressBar pb;

        ProductViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            productName = (TextView)itemView.findViewById(R.id.productName);
            productDesc = (TextView)itemView.findViewById(R.id.productDesc);
            productId   = (TextView) itemView.findViewById(R.id.productId);
            productImg = (ImageView)itemView.findViewById(R.id.productImg);
            productBtn = (Button) itemView.findViewById(R.id.followBtn);
            pb =  (ProgressBar) itemView.findViewById(R.id.loadingImage);
        }
    }

}