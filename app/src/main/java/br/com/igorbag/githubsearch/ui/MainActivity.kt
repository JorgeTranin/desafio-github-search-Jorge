package br.com.igorbag.githubsearch.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService
    val name = "name"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        setupView()
        showUserName()
        setupRetrofit()
        setupListeners()

    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            saveUserLocal(nomeUsuario.text.toString())
            getAllReposByUserName()
        }
    }


    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal(nome: String) {
        //@TODO 3 - Persistir o usuario preenchido na editText com a SharedPref no listener do botao salvar

        if (nome.isNotEmpty()) {
            val sharedPreferences = getPreferences(MODE_PRIVATE) ?: return
            with(sharedPreferences.edit()) {
                putString("nome", nome)
                apply()
            }
        } else {
        }
    }


    //Metodo para buscar sharedpreferences
    fun getSharedPreferences(): String {
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val nome = sharedPreferences.getString("nome", "")
        return nome.toString()

    }

    //Metodo para apresentar o ultimo calculo salvo no cache
    private fun setupCacheResult(resultado: EditText) {
        val shared = getSharedPreferences()
        resultado.hint = shared
    }


    private fun showUserName() {
        //@TODO 4- depois de persistir o usuario exibir sempre as informacoes no EditText  se a sharedpref possuir algum valor, exibir no proprio editText o valor salvo
        setupCacheResult(nomeUsuario)

    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {

        /*
           @TODO 5 -  realizar a Configuracao base do retrofit
           Documentacao oficial do retrofit - https://square.github.io/retrofit/
           URL_BASE da API do  GitHub= https://api.github.com/
           lembre-se de utilizar o GsonConverterFactory mostrado no curso
        */
        val retrofit = Retrofit.Builder().baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        githubApi = retrofit.create(GitHubService::class.java)

    }


    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {
        val textoDoCampo = nomeUsuario.text.toString()
        if (textoDoCampo.isEmpty()) {
            githubApi.getAllRepositoriesByUser(getSharedPreferences())
                .enqueue(object : Callback<List<Repository>> {
                    override fun onResponse(
                        call: Call<List<Repository>>,
                        response: Response<List<Repository>>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                setupAdapter(it)
                            } ?: showErrorSnackbar("A resposta do servidor estava vazia.")
                        } else {
                            showErrorSnackbar("Algo deu errado. Tente novamente mais tarde.")
                        }
                    }

                    override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                        showErrorSnackbar("Erro na requisição. Verifique sua conexão.")
                    }
                })
        }else{
            val username = nomeUsuario.text.toString()
            githubApi.getAllRepositoriesByUser(username)
                .enqueue(object : Callback<List<Repository>> {
                    override fun onResponse(
                        call: Call<List<Repository>>,
                        response: Response<List<Repository>>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                setupAdapter(it)
                            } ?: showErrorSnackbar("A resposta do servidor estava vazia.")
                        } else {
                            showErrorSnackbar("Algo deu errado. Tente novamente mais tarde.")
                        }
                    }

                    override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                        showErrorSnackbar("Erro na requisição. Verifique sua conexão.")
                    }
                })
        }


    }


    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        /*
            @TODO 7 - Implementar a configuracao do Adapter , construir o adapter e instancia-lo
            passando a listagem dos repositorios
         */
        val repositoryAdapter = RepositoryAdapter(list)
        repositoryAdapter.carItemLister = { carro -> openBrowser(carro.htmlUrl) }
        repositoryAdapter.btnShareLister = { carro -> shareRepositoryLink(carro.htmlUrl) }
        listaRepositories.adapter = repositoryAdapter

    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    // @Todo 11 - Colocar esse metodo no click do share item do adapter
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio

    // @Todo 12 - Colocar esse metodo no click item do adapter
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

    private fun showErrorSnackbar(message: String) {
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }

}
