package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(
    private val repositories: List<Repository>,
    private val cardItemListener: (Repository) -> Unit,
    private val btnShareListener: (Repository) -> Unit) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {


    // Cria uma nova view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    // Pega o conteudo da view e troca pela informacao de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val repository = repositories[position]
        holder.nomeRepo.text = repository.name

        holder.btnShare.setOnClickListener {
            btnShareListener(repository)
        }

        holder.itemView.setOnClickListener {
            cardItemListener(repository)
        }
    }


    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomeRepo: TextView = view.findViewById(R.id.tv_nome_do_repo)
        val btnShare: ImageView = view.findViewById(R.id.iv_share)
    }
}


