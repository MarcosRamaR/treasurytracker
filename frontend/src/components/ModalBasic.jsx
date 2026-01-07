

//Modal recives props from parent component
export function Modal({ isOpen, onClose, children, title }) {
  //If modal is not open, return null (not render anything)
    if (!isOpen) return null;

    return (
    <div className="modal-overlay" onClick={onClose}>
        <div className="modal-content" onClick={(e) => e.stopPropagation()}> {/*e.stopPropagation avoids closing the modal when clicking inside it*/}
        <div className="modal-header">
          <h3>{title}</h3> {/* Shows the title passed as a prop */}
            <button className="modal-close" onClick={onClose}>
            &times; {/* X symbol */}
            </button>
        </div>
        {/* Modal body where content is rendered */}
        <div className="modal-body">
          {children} {/* Renders the content that is between the <Modal> tags */}
        </div>
        </div>
    </div>
    );
}