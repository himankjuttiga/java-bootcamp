output "environment" {
  value = var.environment
}

output "region" {
  value = var.region
}

output "sketch_note" {
  value = "null_resource sketch: replace with real modules only after human threat review; never apply to Lab 42 k3d as homework."
}

# Outputs expose non-secret values only (environment, region). Never output a password or endpoint credential.
