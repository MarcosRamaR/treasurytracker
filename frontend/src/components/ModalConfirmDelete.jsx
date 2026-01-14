import { Modal } from './ModalBasic'
import '../styles/ModalStyle.css'

export function ConfirmDeleteModal({ 
    isOpen, 
    onClose, 
    onConfirm,
}) {
    
    const handleConfirm = () => {
        onConfirm()
        onClose()
    }

    return (
        <Modal 
            isOpen={isOpen} 
            onClose={onClose} 
            title="Confirm Delete"
        >
            <div className="modal-form">
                <p style={{ marginBottom: '20px', fontSize: '16px' }}>
                    Are you sure you want to delete all filtered transactions? 
                    This action cannot be undone.
                </p>
                <div className="modal-buttons">
                    <button type="button" className="btn-primary" onClick={handleConfirm}>Delete</button>
                    <button type="button" className="btn-secondary btn-modalConfirmDelete" onClick={onClose}>Cancel</button>

                </div>
            </div>
        </Modal>
    )
}