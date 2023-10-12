package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var nomeUsuario: EditText
    private lateinit var btnConfirmar: Button
    private lateinit var listaRepositories: RecyclerView
    private lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setupListeners()
        showUserName()
        setupRetrofit()
        getAllReposByUserName()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    private fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)

    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            saveUserLocal()
            hideKeyboard()
            getAllReposByUserName()
        }
    }


    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", nomeUsuario.text.toString())
        editor.apply()
    }

    private fun showUserName() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        nomeUsuario.setText(username)
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        githubApi = retrofit.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    private fun getAllReposByUserName() {
        val username = nomeUsuario.text.toString()
        githubApi.getAllRepositoriesByUser(username).enqueue(object : Callback<List<Repository>> {
            override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        setupAdapter(it)
                    }
                }
            }
            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "FAILURE", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        val adapter = RepositoryAdapter(list,
            cardItemListener = { repository ->
                openBrowser(repository.htmlUrl)
            },
            btnShareListener = { repository ->
                shareRepositoryLink(repository.htmlUrl)
            }
        )
        listaRepositories.adapter = adapter
        listaRepositories.layoutManager = LinearLayoutManager(this)

    }

    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}