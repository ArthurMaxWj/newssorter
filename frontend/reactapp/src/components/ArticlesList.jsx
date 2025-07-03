import Button from './Button'

function ArticlesList({ articles, setPage }) {
  return (
    <section id="article-list-page">
      <Button label="Home" action={() => setPage("home")} />

      <h2>All articles:</h2>

      {articles.map((article, index) => (
        <article key={index} className="article">
          <h3>{article.title}</h3>
          <div>
            <span>{article.date}</span> <span>{article.city}</span>
          </div>
          <p>{article.content}</p>
        </article>
      ))}
    </section>
  )
}

export default ArticlesList
