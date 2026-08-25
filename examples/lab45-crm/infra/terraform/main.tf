# Lab 45 — CRM infra sketch (local validate/plan without cloud apply)
# Replace null_resource with real VPC/DB/runtime modules only in an authorized sandbox.
# FORBIDDEN: public database, hardcoded passwords, 0.0.0.0/0 SSH/DB, environment=prod apply.

locals {
  tags = {
    application = "crm"
    environment = var.environment
    managed_by  = "terraform"
    cost_center = "training-crm"
  }
}

resource "null_resource" "crm_stack_sketch" {
  triggers = {
    environment = var.environment
    region      = var.region
  }
  # A real DB would be private-subnet only: no public IP, no 0.0.0.0/0 ingress,
  # reachable in-VPC from the app subnet. The null_resource keeps this laptop-safe.
}
