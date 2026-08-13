import { useState } from 'react'
import { CustomerList } from './components/CustomerList'
import { seedCustomers } from './data/seedCustomers'

export default function App() {
  const [customers] = useState(seedCustomers)
  return (
    <main>
      <h1>Customer Management Platform</h1>
      <CustomerList
        customers={customers}
        onEdit={(customerId) => console.log('edit', customerId, 'lab-request-001')}
      />
    </main>
  )
}
