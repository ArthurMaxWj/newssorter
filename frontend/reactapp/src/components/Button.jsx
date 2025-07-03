import { useState } from 'react'

function Button(props) {

  return <button onClick={props.action} >{props.label}</button>
}

export default Button
