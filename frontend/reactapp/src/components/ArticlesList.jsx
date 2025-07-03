import { useState } from 'react'

function ArticlesList(props) {

  console.log(props)

  return <div>
    All articles: <br/>
    <pre>{props.articles.map( arti => arti.title + "\n")}</pre>
  </div>
}

export default ArticlesList
