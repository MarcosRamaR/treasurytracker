

export const formatCurrency = (amount) => {
  return new Intl.NumberFormat('es-ES', //JS native way to format currency
    { style: 'currency', currency: 'EUR' }).format(amount);
}

export const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('es-ES')
}