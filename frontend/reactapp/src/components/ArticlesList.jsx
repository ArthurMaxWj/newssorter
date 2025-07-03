import { useState } from 'react'
import Button from './Button'

function ArticlesList({ articles, setPage }) {
  const [search, setSearch] = useState("")
  const [selection, setSelection] = useState("BOTH")


  const handleSearchChange = (event) => {
    setSearch(event.target.value)
  }

  const handleRadioChange = (e) => {
    setSelection(e.target.value)
  }

  const okKind = (articleKind, matchAgaint) => {
    return articleKind == matchAgaint || matchAgaint == "BOTH";
  }

  return (
    <section id="article-list-page">
      <Button label="Home" action={() => setPage("home")} />

      <h1>All articles:</h1>
      <div id="search-box" >
        <input type="text" onChange={handleSearchChange} placeholder="Search by city or leave blank to see all..." />
      </div>
      <div id="radio-box">
    <div>
      <span>What kind of news?</span>

      <label>
        <input
          type="radio"
          value="LOCAL"
          checked={selection === 'LOCAL'}
          onChange={handleRadioChange}
        />
        <span>Only local</span>
      </label>

      <br />

      <label>
        <input
          type="radio"
          value="GLOBAL"
          checked={selection === 'GLOBAL'}
          onChange={handleRadioChange}
        />
        <span>Only global</span>
      </label>

      <br />

      <label>
        <input
          type="radio"
          value="BOTH"
          checked={selection === 'BOTH'}
          onChange={handleRadioChange}
        />
        <span>Both</span> 
      </label>
    </div>

      </div>
      <div class="article-list">
        {articles.map((article, index) => {
          if ((article.city.startsWith(search) || search.trim() == "") && okKind(article.kind, selection))
            return (
            <article key={index} className="article">
              <h3 class="article-title">[{article.kind}] {article.title}</h3>
              <div>
                <span>{article.date}</span> <mark>{article.city}</mark>
              </div>
              <p>{article.content}</p>
            </article>
            )
        })}
      </div>
    </section>
  )
}

export default ArticlesList
