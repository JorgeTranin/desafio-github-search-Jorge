package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository
class RepositoryListAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryListAdapter.ViewHolder>() {

    var repositoryClickListener: (Repository) -> Unit = {}
    var shareButtonClickListener: (Repository) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repositoryItem = repositories[position]
        holder.bind(repositoryItem)
    }

    override fun getItemCount(): Int = repositories.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageShare: ImageView = view.findViewById(R.id.iv_favorite)
        val textRepositoryName: TextView = view.findViewById(R.id.tv_preco)

        init {
            view.setOnClickListener {
                repositoryClickListener(repositories[adapterPosition])
            }
            imageShare.setOnClickListener {
                shareButtonClickListener(repositories[adapterPosition])
            }
        }

        fun bind(repository: Repository) {
            textRepositoryName.text = repository.name
        }
    }
}



