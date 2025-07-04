function Button(props) {

  return <button onClick={props.action} className={props.btnClass || "rect-button"} >{props.label}</button>
}

export default Button
