import { useState } from 'react'
import './App.css'
import HomePage from './pages/HomePage'
import ShowAllStaticPage from './pages/ShowAllStaticPage'
import ShowAllDynamicPage from './pages/ShowAllDynamicPage'


function App() {
  const [store, setStore] = useState({
    staticArticles: [],
    dynamicArticles: [],
  })

  const [isForced, setIsForced] = useState(false)

  const [home, showAllStatic, showAllDynamic] = ["home", "showAllStatic", "showAllDynamic"]
  const [page, setPage] = useState(home)

  switch (page) {
    case home:
        return <HomePage store={store} setPage={setPage} isForced={isForced} setIsForced={setIsForced}  />
    case showAllStatic:
      return <ShowAllStaticPage store={store} setStore={setStore} setPage={setPage}  />

    case showAllDynamic:
      return <ShowAllDynamicPage store={store} setStore={setStore} setPage={setPage}  />
    default:
      return <div>Error: unknown page</div>
  }
}

export default App
